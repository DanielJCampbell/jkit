/*
 * FindBugs - Find bugs in Java programs
 * Copyright (C) 2005, University of Maryland
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package edu.umd.cs.findbugs.detect;

import org.apache.bcel.classfile.Code;

import edu.umd.cs.findbugs.BugInstance;
import edu.umd.cs.findbugs.BugReporter;
import edu.umd.cs.findbugs.BytecodeScanningDetector;
import edu.umd.cs.findbugs.StatelessDetector;

public class BadUseOfReturnValue extends BytecodeScanningDetector {

	BugReporter bugReporter;

	public BadUseOfReturnValue(BugReporter bugReporter) {
		this.bugReporter = bugReporter;
	}



	boolean readLineOnTOS = false;
	boolean stringIndexOfOnTOS= false;
	@Override
         public void visit(Code obj) {
		stringIndexOfOnTOS= false;
		readLineOnTOS = false;
		super.visit(obj);
	}


	@Override
         public void sawOpcode(int seen) {
		if (seen == INVOKEVIRTUAL && 
			getNameConstantOperand().equals("indexOf")
			&& getClassConstantOperand().equals("java/lang/String")
			&& getSigConstantOperand().equals("(Ljava/lang/String;)I"))
		   stringIndexOfOnTOS= true;
		else if (stringIndexOfOnTOS) {
			if (seen == IFLE || seen == IFGT)
			       bugReporter.reportBug(new BugInstance(this, "RV_CHECK_FOR_POSITIVE_INDEXOF", LOW_PRIORITY)
                                .addClassAndMethod(this)
                                .addSourceLine(this));
			stringIndexOfOnTOS = false;
		}

		if (seen == INVOKEVIRTUAL && 
			getNameConstantOperand().equals("readLine")
			&& getSigConstantOperand().equals("()Ljava/lang/String;"))
		  readLineOnTOS = true;
		else if (readLineOnTOS) {
			if (seen == IFNULL || seen == IFNONNULL)
			       bugReporter.reportBug(new BugInstance(this, "RV_DONT_JUST_NULL_CHECK_READLINE", NORMAL_PRIORITY)
                                .addClassAndMethod(this)
                                .addSourceLine(this));

			readLineOnTOS = false;
			}
	}

}
