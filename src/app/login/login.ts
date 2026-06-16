import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { AuthService } from '../services/auth';

@Component({
  selector: 'app-login',
  imports: [FormsModule, CommonModule],
  templateUrl: './login.html',
  styleUrl: './login.css'
})
export class Login {
  mode: 'student' | 'admin' = 'student';

  // student fields
  rollNumber = '';
  dateOfBirth = '';

  // admin fields
  username = '';
  password = '';

  error = '';
  loading = false;

  constructor(private authService: AuthService, private router: Router) {}

  login() {
    this.loading = true;
    this.error = '';

    const identifier = this.mode === 'admin' ? this.username : this.rollNumber;
    const credential = this.mode === 'admin' ? this.password : this.dateOfBirth;

    this.authService.login(identifier, credential).subscribe({
      next: () => this.router.navigate(['/students']),
      error: () => {
        this.error = this.mode === 'admin'
          ? 'Invalid username or password'
          : 'Invalid roll number or date of birth';
        this.loading = false;
      }
    });
  }

  switchMode(m: 'student' | 'admin') {
    this.mode = m;
    this.error = '';
  }
}
