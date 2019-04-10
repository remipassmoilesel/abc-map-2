import {Component, OnDestroy, OnInit} from '@angular/core';
import * as _ from 'lodash';
import {IMainState} from '../../store';
import {Store} from '@ngrx/store';
import {MapModule} from '../../store/map/map-actions';
import {Actions, ofType} from '@ngrx/effects';
import {Subscription} from 'rxjs';
import {RxUtils} from '../../lib/utils/RxUtils';
import ActiveForegroundColorChanged = MapModule.ActiveForegroundColorChanged;
import ActiveBackgroundColorChanged = MapModule.ActiveBackgroundColorChanged;
import ActionTypes = MapModule.ActionTypes;

declare type ColorType = 'foreground' | 'background';

@Component({
  selector: 'abc-draw-color-picker',
  templateUrl: './draw-color-picker.component.html',
  styleUrls: ['./draw-color-picker.component.scss']
})
export class DrawColorPickerComponent implements OnInit, OnDestroy {

  activeColorType: ColorType = 'foreground';
  activeForegroundColor = 'rgb(255,255,255)';
  activeBackgroundColor = 'rgb(255,255,255)';

  selectedColor: string = 'black';
  colorHistory: string[] = [];

  colorPresets = [
    'rgb(255,7,20)',
    'rgb(255,157,36)',
    'rgb(255,245,0)',
    'rgb(65,255,73)',
    'rgb(37,33,236)',
    'rgb(142,10,208)'
  ];

  colorChanged$?: Subscription;

  constructor(private store: Store<IMainState>,
              private actions$: Actions) {
  }

  ngOnInit() {
    this.fillColorHistory();
    this.listenColorChanges();
  }

  ngOnDestroy(): void {
    RxUtils.unsubscribe(this.colorChanged$);
  }

  onPresetSelected(color: string) {
    this.selectedColor = color;
    this.selectColor(this.selectedColor);
  }

  onColorChanged(color: string) {
    this.selectColor(color);
  }

  selectColor(color: string) {
    this.colorHistory.unshift(color);
    this.colorHistory.pop();

    if (this.activeColorType === 'foreground') {
      this.setForegroundColor(color);
    } else {
      this.setBackgroundColor(color);
    }
  }

  setForegroundColor(color: string) {
    this.store.dispatch(new ActiveForegroundColorChanged(color));
  }

  setBackgroundColor(color: string) {
    this.store.dispatch(new ActiveBackgroundColorChanged(color));
  }

  fillColorHistory() {
    _.times(this.colorPresets.length, i => this.colorHistory.push('rgb(255,255,255'));
  }

  listenColorChanges() {
    this.colorChanged$ = this.actions$
      .pipe(
        ofType(
          ActionTypes.ACTIVE_FOREGROUND_COLOR_CHANGED,
          ActionTypes.ACTIVE_BACKGROUND_COLOR_CHANGED,
        ),
      )
      .subscribe((action: ActiveForegroundColorChanged | ActiveBackgroundColorChanged) => {
        if (action.type === ActionTypes.ACTIVE_FOREGROUND_COLOR_CHANGED) {
          this.activeForegroundColor = action.color;
        } else {
          this.activeBackgroundColor = action.color;
        }
      });
  }
}