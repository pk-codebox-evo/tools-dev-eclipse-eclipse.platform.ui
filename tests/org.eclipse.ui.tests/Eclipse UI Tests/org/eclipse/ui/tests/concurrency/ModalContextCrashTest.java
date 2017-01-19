/*******************************************************************************
 * Copyright (c) 2005, 2014 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Jeanderson Candido <http://jeandersonbc.github.io> - Bug 433603
 *******************************************************************************/
package org.eclipse.ui.tests.concurrency;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.operation.IThreadListener;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

import junit.framework.TestCase;

/**
 * Makes ModalContext thread crash and hang the IDE
 */
public class ModalContextCrashTest extends TestCase {

	public void testCrash() throws Exception {
		IRunnableWithProgress operation = new CrashingRunnable();
		try{
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().run(true, false, operation);
			fail("Should have an invocation target exception");
		}
		catch (InvocationTargetException e){
			//We should get this
		}
		if (Thread.interrupted()) {
			fail("Thread was interrupted at end of test");
		}
	}

	private static final class CrashingRunnable implements IRunnableWithProgress, IThreadListener {

		@Override
		public void run(IProgressMonitor monitor) {
		}

		@Override
		public void threadChange(Thread thread) {
			// only throw the exception in the finally block
			// of ModalContextThread
			if (Display.findDisplay(thread) != null) {
				throw new RuntimeException("Simulated exception during threadChange");
			}
		}
	}

}
