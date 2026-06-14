import { Routes } from '@angular/router';
import { Login } from './login/login';
import { Students } from './students/students';
import { authGuard } from './guards/auth-guard';

export const routes: Routes = [
  { path: 'login', component: Login },
  { path: 'students', component: Students, canActivate: [authGuard] },
  { path: '', redirectTo: '/students', pathMatch: 'full' },
  { path: '**', redirectTo: '/students' }
];
