/*
 * Copyright (c) 2020 the original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

import {ChangeDetectionStrategy, Component, OnInit} from '@angular/core';
import {Organization} from '../../data/organization';
import {OrganizationHeaderFacade} from './organization-header.facade';
import {Observable} from 'rxjs';
import {CreateDialogComponent} from '../create-dialog/create-dialog.component';
import {MatDialog} from '@angular/material/dialog';
import {EditDialogComponent} from '../edit-dialog/edit-dialog.component';
import {DeleteDialogComponent} from '../delete-dialog/delete-dialog.component';
import {take, switchMap} from 'rxjs/operators';

@Component({
  selector: 'app-organization-header',
  templateUrl: './organization-header.component.html',
  styleUrls: ['./organization-header.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class OrganizationHeaderComponent implements OnInit {
  public organizations$: Observable<Organization[]>;
  public userRole$: Observable<string | null>;
  public selectedId$: Observable<any>;

  constructor(private organizationHeaderFacade: OrganizationHeaderFacade, public dialog: MatDialog) {
    organizationHeaderFacade.initSubscriptions();
  }

  public ngOnInit(): void {
    this.organizations$ = this.organizationHeaderFacade.organizations$;
    this.userRole$ = this.organizationHeaderFacade.userRole$;
    this.selectedId$ = this.organizationHeaderFacade.selectedId$;
  }

  public createNew(): void {
    const dialog = this.dialog.open(CreateDialogComponent, {
      width: '300px',
      data: {
        entityType: 'organization',
        name: ''
      }
    });

    dialog.afterClosed().subscribe(res => {
      if (res) { this.organizationHeaderFacade.add({ name: res }); }
    });
  }

  public edit(): void {
    let currentName = '';
    this.organizationHeaderFacade.organizationName$.subscribe(name => {
      currentName = name;
    });
    const dialog = this.dialog.open(EditDialogComponent, {
      width: '300px',
      data: {
        entityType: 'organization',
        entityName: currentName,
        name: ''
      }
    });

    dialog.afterClosed().subscribe(res => {
      if (res) { this.organizationHeaderFacade.rename(res); }
    });
  }

  public selectOrganization(id): void {
    this.organizationHeaderFacade.select(id);
  }

  public rename(name): void {
    this.organizationHeaderFacade.rename(name);
  }

  public delete(): void {
    this.organizationHeaderFacade.organizationName$
      .pipe(
        take(1),
        switchMap(currentName => {
          return this.dialog.open(DeleteDialogComponent, {
            width: '400px',
            data: {
              entityType: 'organization',
              entityName: currentName
            }
          }).afterClosed();
        })
      )
      .subscribe(res => {
        if (res) { this.organizationHeaderFacade.delete(); }
      });
  }
}