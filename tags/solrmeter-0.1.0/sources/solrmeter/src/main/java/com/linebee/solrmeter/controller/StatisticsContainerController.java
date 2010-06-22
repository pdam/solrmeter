/**
 * Copyright Linebee. www.linebee.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.linebee.solrmeter.controller;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import com.linebee.solrmeter.view.StatisticsContainer;


public class StatisticsContainerController {

	private StatisticsContainer container;
	
	private Timer timer = null;
	
	public StatisticsContainerController(StatisticsContainer view) {
		this.container = view;
		timer = new Timer();
		TimerTask task = new TimerTask() {

			@Override
			public void run() {
				container.refresh();
			}
			
		};
		timer.schedule(task, new Date(), 2000);
	}
	
	public void onTabChanged() {
		Thread thread = new Thread() {
			@Override
			public void run() {
				container.refresh();
			}
		};
		thread.start();
	}
}