import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { StudentService } from '../services/student';
import { AuthService } from '../services/auth';

@Component({
  selector: 'app-students',
  imports: [FormsModule, CommonModule],
  templateUrl: './students.html',
  styleUrl: './students.css'
})
export class Students implements OnInit {
  students: any[] = [];
  totalPages = 0;
  currentPage = 0;
  pageSize = 5;
  sortBy = 'id';
  direction = 'asc';
  searchName = '';
  error = '';

  showForm = false;
  editingId: number | null = null;
  form = { name: '', email: '', course: '' };

  constructor(private studentService: StudentService, public authService: AuthService) {}

  ngOnInit() {
    this.loadStudents();
  }

  loadStudents() {
    this.studentService.getAll(this.currentPage, this.pageSize, this.sortBy, this.direction).subscribe({
      next: res => {
        this.students = res.content;
        this.totalPages = res.totalPages;
        this.error = '';
      },
      error: () => this.error = 'Failed to load students'
    });
  }

  search() {
    if (!this.searchName.trim()) {
      this.loadStudents();
      return;
    }
    this.studentService.search(this.searchName).subscribe({
      next: res => {
        this.students = res;
        this.totalPages = 1;
      },
      error: () => this.error = 'Search failed'
    });
  }

  openAdd() {
    this.editingId = null;
    this.form = { name: '', email: '', course: '' };
    this.showForm = true;
  }

  openEdit(student: any) {
    this.editingId = student.id;
    this.form = { name: student.name, email: student.email, course: student.course };
    this.showForm = true;
  }

  save() {
    if (this.editingId) {
      this.studentService.update(this.editingId, this.form).subscribe({
        next: () => { this.showForm = false; this.loadStudents(); },
        error: err => this.error = err.error?.error || 'Update failed'
      });
    } else {
      this.studentService.create(this.form).subscribe({
        next: () => { this.showForm = false; this.loadStudents(); },
        error: err => this.error = err.error?.error || 'Create failed'
      });
    }
  }

  delete(id: number) {
    if (!confirm('Delete this student?')) return;
    this.studentService.delete(id).subscribe({
      next: () => this.loadStudents(),
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

  logout() {
    this.authService.logout();
  }
}
