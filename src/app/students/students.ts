import { Component, OnInit, OnDestroy } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { Subject, Subscription } from 'rxjs';
import { debounceTime, distinctUntilChanged } from 'rxjs/operators';
import { StudentService } from '../services/student';
import { AuthService } from '../services/auth';

@Component({
  selector: 'app-students',
  imports: [FormsModule, CommonModule],
  templateUrl: './students.html',
  styleUrl: './students.css'
})
export class Students implements OnInit, OnDestroy {
  isAdmin = false;

  // Admin state
  students: any[] = [];
  totalPages = 0;
  totalStudents = 0;
  currentPage = 0;
  pageSize = 5;
  sortBy = 'id';
  direction = 'asc';

  // Search & filter
  searchName = '';
  selectedCourse = '';
  courses: string[] = [];
  isFiltered = false;
  filteredCount = 0;

  private searchSubject = new Subject<string>();
  private searchSub?: Subscription;

  showForm = false;
  editingId: number | null = null;
  form = { name: '', email: '', course: '', rollNumber: '', dateOfBirth: '' };

  // Student (user) state
  myProfile: any = null;
  branchCount = 0;

  error = '';

  constructor(private studentService: StudentService, public authService: AuthService) {}

  ngOnInit() {
    this.isAdmin = this.authService.isAdmin();
    if (this.isAdmin) {
      this.loadStudents();
      this.loadCourses();
      this.searchSub = this.searchSubject.pipe(
        debounceTime(350),
        distinctUntilChanged()
      ).subscribe(() => this.applyFilters());
    } else {
      this.loadMyProfile();
    }
  }

  ngOnDestroy() {
    this.searchSub?.unsubscribe();
  }

  // ── Admin: load ───────────────────────────────

  loadStudents() {
    this.studentService.getAll(this.currentPage, this.pageSize, this.sortBy, this.direction).subscribe({
      next: res => {
        this.students = res.content;
        this.totalPages = res.totalPages;
        this.totalStudents = res.totalElements;
        this.isFiltered = false;
        this.error = '';
      },
      error: () => this.error = 'Failed to load students'
    });
  }

  loadCourses() {
    this.studentService.getCourses().subscribe({
      next: courses => this.courses = courses,
      error: () => {}
    });
  }

  // ── Admin: search & filter ────────────────────

  onSearchInput() {
    this.searchSubject.next(this.searchName);
  }

  onCourseChange() {
    this.applyFilters();
  }

  applyFilters() {
    const name = this.searchName.trim();
    const course = this.selectedCourse;
    if (!name && !course) {
      this.currentPage = 0;
      this.loadStudents();
      return;
    }
    this.studentService.filter(name, course).subscribe({
      next: res => {
        this.students = res;
        this.filteredCount = res.length;
        this.totalPages = 1;
        this.isFiltered = true;
        this.error = '';
      },
      error: () => this.error = 'Filter failed'
    });
  }

  clearFilters() {
    this.searchName = '';
    this.selectedCourse = '';
    this.currentPage = 0;
    this.loadStudents();
  }

  // ── Admin: CRUD ───────────────────────────────

  openAdd() {
    this.editingId = null;
    this.form = { name: '', email: '', course: '', rollNumber: '', dateOfBirth: '' };
    this.error = '';
    this.showForm = true;
  }

  openEdit(student: any) {
    this.editingId = student.id;
    this.form = {
      name: student.name,
      email: student.email,
      course: student.course,
      rollNumber: student.rollNumber ?? '',
      dateOfBirth: student.dateOfBirth ?? ''
    };
    this.error = '';
    this.showForm = true;
  }

  save() {
    const payload = { ...this.form };
    if (this.editingId) {
      this.studentService.update(this.editingId, payload).subscribe({
        next: () => { this.showForm = false; this.isFiltered ? this.applyFilters() : this.loadStudents(); },
        error: err => this.error = err.error?.error || 'Update failed'
      });
    } else {
      this.studentService.create(payload).subscribe({
        next: () => { this.showForm = false; this.loadStudents(); },
        error: err => this.error = err.error?.error || 'Create failed'
      });
    }
  }

  delete(id: number) {
    if (!confirm('Delete this student?')) return;
    this.studentService.delete(id).subscribe({
      next: () => this.isFiltered ? this.applyFilters() : this.loadStudents(),
      error: () => this.error = 'Delete failed'
    });
  }

  changePage(page: number) {
    this.currentPage = page;
    this.loadStudents();
  }

  changeSort(field: string) {
    if (this.sortBy === field) {
      this.direction = this.direction === 'asc' ? 'desc' : 'asc';
    } else {
      this.sortBy = field;
      this.direction = 'asc';
    }
    this.loadStudents();
  }

  get hasFilters(): boolean {
    return !!(this.searchName.trim() || this.selectedCourse);
  }

  // ── Student (user) ────────────────────────────

  loadMyProfile() {
    this.studentService.getMe().subscribe({
      next: profile => {
        this.myProfile = profile;
        this.loadBranchCount(profile.course);
      },
      error: () => this.error = 'Failed to load your profile'
    });
  }

  loadBranchCount(course: string) {
    this.studentService.countByCourse(course).subscribe({
      next: res => this.branchCount = res.count,
      error: () => {}
    });
  }

  logout() {
    this.authService.logout();
  }
}
