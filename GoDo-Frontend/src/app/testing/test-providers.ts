import { EnvironmentProviders, Provider } from '@angular/core';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { provideNoopAnimations } from '@angular/platform-browser/animations';
import { provideRouter } from '@angular/router';
import { MessageService } from 'primeng/api';
import { providePrimeNG } from 'primeng/config';
import { MyTheme } from '../../themes/my-theme';

export const testProviders: Array<EnvironmentProviders | Provider> = [
  provideNoopAnimations(),
  providePrimeNG({
    theme: {
      preset: MyTheme,
    },
  }),
  provideHttpClient(),
  provideHttpClientTesting(),
  provideRouter([]),
  MessageService,
];
