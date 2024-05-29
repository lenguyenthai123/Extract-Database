import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ColumnTableComponent } from './column-table.component';

describe('ColumnTableComponent', () => {
  let component: ColumnTableComponent;
  let fixture: ComponentFixture<ColumnTableComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ColumnTableComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(ColumnTableComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
