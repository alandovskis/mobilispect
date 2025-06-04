import {Component} from '@angular/core';
import {NgOptimizedImage} from '@angular/common';

@Component({
  selector: 'app-header',
  imports: [
    NgOptimizedImage
  ],
  templateUrl: './app-header.html',
})
export class AppHeader {
  navShown = false;
  onNavClick() {
    let classList = document.getElementsByTagName('nav')[0].classList;
    if (this.navShown) {
      classList.remove('visible')
      classList.add('collapse')
    } else {
      classList.add('visible')
      classList.remove('collapse')
    }
    this.navShown = !this.navShown
  }
}
