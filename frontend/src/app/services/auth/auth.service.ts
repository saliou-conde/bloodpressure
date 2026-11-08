import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface LoginRequest {
  username: string;
  password: string;
}

export interface RegisterRequest {
  username: string;
  password: string;
  email: string;
}

export interface LoginResponse {
  token: string;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private http = inject(HttpClient);
  private readonly apiUrl = `${environment.backendUrl}/auth`;

  login(credentials: LoginRequest): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${this.apiUrl}/login`, credentials).pipe(
      tap(response => this.saveToken(response.token))
    );
  }

  register(data: RegisterRequest): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${this.apiUrl}/register`, data).pipe(
      tap(response => this.saveToken(response.token))
    );
  }

  saveToken(token: string): void {
    localStorage.setItem('auth_token', token);
  }

  getToken(): string | null {
    return localStorage.getItem('auth_token');
  }

  logout(): void {
    localStorage.removeItem('auth_token');
  }

  isLoggedIn(): boolean {
    return !!this.getToken();
  }

 isTokenValid(): boolean {
   const token = this.getToken();
   if (!token) {
     return false;
   }

   try {
     // Ein JWT besteht aus 3 Teilen: Header.Payload.Signature
     // Wir nehmen den mittleren Teil (Payload) und dekodieren ihn
     const payloadPart = token.split('.')[1];
     const decodedPayload = JSON.parse(atob(payloadPart));

     // Prüfen, ob das Ablaufdatum ('exp') in der Zukunft liegt
     const expirationTime = decodedPayload.exp * 1000; // JWT exp ist in Sekunden, JS braucht Millisekunden
     return Date.now() < expirationTime;
   } catch (e) {
     // Falls das Token beschädigt/ungültig formatiert ist
     return false;
   }
 }
}
