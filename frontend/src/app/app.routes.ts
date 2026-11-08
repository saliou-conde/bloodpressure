import { Routes } from '@angular/router';
import { LoginComponent } from './components/auth/login.component';
import { RegisterComponent } from './components/auth/register.component';
import { PatientListComponent } from './components/patient-list/patient-list.component/patient-list.component';
import { authGuard, guestGuard } from './guard/auth.guard';
import { ForgotPasswordComponent } from './components/auth/forgot-password.component';
import { ResetPasswordComponent } from './components/auth/reset-password.component';

export const routes: Routes = [
    {
      path: 'login',
      component: LoginComponent,
      canActivate: [guestGuard] // Wenn eingeloggt, hier nicht rein
    },
    {
      path: 'register', component: RegisterComponent,
      canActivate: [guestGuard] // Wenn eingeloggt, hier nicht rein
    },
    // ... deine bestehenden Routen (login, register, patients, etc.)
      {
        path: 'forgot-password',
        component: ResetPasswordComponent,
        canActivate: [guestGuard]
      },
      {
        path: 'patients', component: PatientListComponent,
        canActivate: [authGuard] // Nur mit gültigem Token erreichbar
      },
      {
        path: '',
        redirectTo: 'login',
        pathMatch: 'full'
      },
      {
        path: '**',
        redirectTo: 'patients'
      }
  ];
