import { Component, OnInit, Input } from '@angular/core';
import { FormControl } from '@angular/forms';
import { Tabs } from '../tabs/tabs.component';

@Component( {
    selector: 'tab',
    templateUrl: './tab.component.html',
    styleUrls: ['./tab.component.css']
} )

export class Tab {
    @Input() tabTitle: string;
    @Input() errorCount: number;
    public active: boolean = false;
    
    constructor(tabs:Tabs) {
      tabs.addTab(this);
    }
}
