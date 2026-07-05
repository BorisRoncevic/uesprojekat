import { Component, EventEmitter, Input, Output } from '@angular/core';
import { ButtonModule } from 'primeng/button';
import { FloatLabelModule } from 'primeng/floatlabel';
import { CommonModule } from '@angular/common';
import { InputTextModule } from 'primeng/inputtext';
import {
  FormControl,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';

import { TooltipModule } from 'primeng/tooltip';
import { AuthService } from '../../../services/auth/auth.service';
import { MessageService } from 'primeng/api';

@Component({
  selector: 'app-register-form',
  imports: [
    FloatLabelModule,
    ButtonModule,
    CommonModule,
    InputTextModule,
    ReactiveFormsModule,
    TooltipModule,
  ],
  templateUrl: './register-form.component.html',
  styleUrl: './register-form.component.css',
})
export class RegisterFormComponent {
  @Output() switchToLogin = new EventEmitter<void>();
  @Output() registerSuccess = new EventEmitter<void>();

  public loading: boolean = false;

  registerForm = new FormGroup({
    email: new FormControl('', [Validators.required, Validators.email]),
    username: new FormControl('', [
      Validators.required,
      Validators.pattern('^[a-zA-Z0-9]{5,15}$'),
    ]),
    password: new FormControl('', [
      Validators.required,
      Validators.pattern(
        '^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+.!=]).{6,20}$'
      ),
    ]),
  });

  constructor(
    public authService: AuthService,
    public messageService: MessageService
  ) {}

  isValid() {
    return this.registerForm.valid;
  }

  onSignInClick(): void {
    this.switchToLogin.emit();
  }

  onRegisterSuccess(): void {
    this.registerSuccess.emit();
  }

  submitRegisterForm() {
    this.loading = true;
    this.authService.sendRegistrationRequest({
        username: this.registerForm.value.username,
        email: this.registerForm.value.email,
        password: this.registerForm.value.password,
      })
      .subscribe({
        next: (next) => {
          this.messageService.add({
            severity: 'success',
            summary: 'Successful registration',
            detail: 'You have registered successfully. You can log in now.',
          });

          this.registerForm.reset();
          this.loading = false;
          this.onSignInClick();
        },
        error: (error) => {
          this.messageService.add({
            severity: 'error',
            summary: 'Unsuccessful registration',
            detail: error.error.message,
          });

          this.loading = false;
        },
      });
  }
}
