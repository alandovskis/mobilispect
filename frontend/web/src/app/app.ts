import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import {AppNav} from './nav/app-nav.component';
import {AppHeader} from './header/app-header.component';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, AppHeader, AppNav],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {
}
