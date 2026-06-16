import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';

@Injectable({ providedIn: 'root' })
export class StudentService {
  private apiUrl = 'http://localhost:8081/students';

  constructor(private http: HttpClient) {}

  getAll(page = 0, size = 10, sortBy = 'id', direction = 'asc') {
    const params = new HttpParams()
      .set('page', page)
      .set('size', size)
      .set('sortBy', sortBy)
      .set('direction', direction);
    return this.http.get<any>(this.apiUrl, { params });
  }

  getById(id: number) {
    return this.http.get<any>(`${this.apiUrl}/${id}`);
  }

  getMe() {
    return this.http.get<any>(`${this.apiUrl}/me`);
  }

  countByCourse(course: string) {
    return this.http.get<any>(`${this.apiUrl}/count/course/${encodeURIComponent(course)}`);
  }

  create(student: any) {
    return this.http.post<any>(this.apiUrl, student);
  }

  update(id: number, student: any) {
    return this.http.put<any>(`${this.apiUrl}/${id}`, student);
  }

  delete(id: number) {
    return this.http.delete<any>(`${this.apiUrl}/${id}`);
  }

  search(name: string) {
    return this.http.get<any>(`${this.apiUrl}/search`, { params: { name } });
  }

  filter(query: string, course: string) {
    return this.http.get<any[]>(`${this.apiUrl}/filter`, { params: { query, course } });
  }

  getCourses() {
    return this.http.get<string[]>(`${this.apiUrl}/courses`);
  }
}
