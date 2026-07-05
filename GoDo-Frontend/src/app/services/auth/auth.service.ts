import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { EMPTY, Observable, throwError } from 'rxjs';
import { environment } from '../../../environments/environment.development';
import { AccountRegistrationRequest } from '../../models/auth/requests/accountRegistrationRequest';
import { LoginRequest } from '../../models/auth/requests/loginRequest';
import { TokenDto } from '../../models/auth/jwt/tokenDto';
import { jwtDecode } from 'jwt-decode';
import { MyJwtPayload } from '../../models/auth/jwt/myJwtPayload';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  constructor(private http: HttpClient) {}

  private clearStoredTokens(): void {
    if (typeof window !== 'undefined' && window.localStorage) {
      localStorage.removeItem('accessToken');
      localStorage.removeItem('refreshToken');
    }
  }

  private decodeAccessToken(): MyJwtPayload | null {
    const token = this.getAccessToken();

    if (token == null) {
      return null;
    }

    try {
      return jwtDecode<MyJwtPayload>(token);
    } catch {
      this.clearStoredTokens();
      return null;
    }
  }

  public sendRegistrationRequest(
    request: AccountRegistrationRequest
  ): Observable<any> {
    return this.http.post(environment.apiUrl + '/auth/register', request);
  }

  public sendLoginRequest(request: LoginRequest): Observable<any> {
    return this.http.post(environment.apiUrl + '/auth/login', request);
  }

  public getUserRole() {
    return this.decodeAccessToken()?.role ?? null;
  }

  setAccessToken(token: string) {
    if (typeof window !== 'undefined' && window.localStorage) {
      localStorage.setItem('accessToken', token);
    }
  }

  public setTokens(token: TokenDto) {
    if (typeof window !== 'undefined' && window.localStorage) {
      localStorage.setItem('accessToken', token.accessToken);
      localStorage.setItem('refreshToken', token.refreshToken);
    }
  }

  getRefreshToken() {
    if (typeof window !== 'undefined' && window.localStorage) {
      return localStorage.getItem('refreshToken');
    }
    return null;
  }

  getAccessToken(): string | null {
    if (typeof window !== 'undefined' && window.localStorage) {
      return localStorage.getItem('accessToken');
    }
    return null;
  }

  isLoggedIn() {
    if (typeof window !== 'undefined' && window.localStorage) {
      if (
        localStorage.getItem('accessToken') != null &&
        localStorage.getItem('refreshToken') != null
      ) {
        return true;
      }
    }

    return false;
  }

  logout() {
    let refreshTokenId: any = "";
    if (typeof window !== 'undefined' && window.localStorage) {
      refreshTokenId = localStorage.getItem("refreshToken");
      localStorage.clear();
    }
    return this.http.delete(`${environment.apiUrl}/auth/logout/${refreshTokenId}`);
  }

  getUsername() {
    return this.decodeAccessToken()?.sub ?? '??????';
  }

  isAdmin(): boolean {
    return this.decodeAccessToken()?.role == 'ADMIN';
  }

  refreshToken(): Observable<any> {
    let refreshToken: any = '';
    if (typeof window !== 'undefined' && window.localStorage) {
      refreshToken = localStorage.getItem('refreshToken');
    } else {
      return EMPTY;
    }
    if (refreshToken == null) {
      this.logout();
      return throwError(() => new Error('No refresh token available'));
    }

    return this.http.get(
      environment.apiUrl + '/auth/refresh-token/' + refreshToken
    );
  }

  isProfilePending(): boolean {
    return this.decodeAccessToken()?.status == 'PENDING';
  }
}
