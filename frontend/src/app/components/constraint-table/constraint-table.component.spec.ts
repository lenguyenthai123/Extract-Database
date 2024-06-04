import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ConstraintTableComponent } from './constraint-table.component';

describe('ConstraintTableComponent', () => {
  let component: ConstraintTableComponent;
  let fixture: ComponentFixture<ConstraintTableComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ConstraintTableComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(ConstraintTableComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
