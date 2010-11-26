/**
 * Copyright Linebee LLC
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
package com.linebee.solrmeter.model.operation;

import org.apache.log4j.Logger;

import com.linebee.solrmeter.model.exception.OperationException;
import com.linebee.stressTestScope.StressTestScope;

/**
 * Worker that executes an operation every N milisecods, represented by the
 * 'timeToWait' field.
 * The operation is executed on a different thread, that way the operation time
 * does not affect the interval of this worker. For example, if the worker has a
 * timeToWait of 20 seconds and it executes a query that takes 10 seconds, the next
 * query will be executed 20 seconds after the last one STARTED (10 seconds after it
 * finished).
 * @author tflobbe
 *
 */
@StressTestScope
public class ConstantOperationExecutorThread extends Thread {
	
	private long timeToWait;
	
	private boolean running;
	
	private Operation operation;
	
	public ConstantOperationExecutorThread(Operation operation) {
		super();
		this.operation = operation;
	}
	
	@Override
	public synchronized void run() {
		while(running) {
			try {
				this.wait(new Long(getTimeToWait()));
				if(running) {
					executeOperation();
				}
			} catch (InterruptedException e) {
				Logger.getLogger(this.getClass()).error("Error on query thread", e);
				throw new RuntimeException(e);
			} catch (OperationException e) {
				Logger.getLogger(this.getClass()).error("Error on query thread", e);
				throw new RuntimeException(e);
			}
		}
	}
	
	@Override
	public synchronized void start() {
		this.running = true;
		super.start();
	}
	
	public synchronized void wake() {
		this.notify();
	}
	
	@Override
	public void destroy() {
		this.running = false;
	}
	
	private void executeOperation() throws OperationException {
		Thread thread = new Thread() {
			
			@Override
			public void run() {
				operation.execute();
			}
		};
		thread.run();
	}

	private long getTimeToWait() {
		return timeToWait;
	}

	public void setTimeToWait(long timeToWait) {
		this.timeToWait = timeToWait;
	}
}