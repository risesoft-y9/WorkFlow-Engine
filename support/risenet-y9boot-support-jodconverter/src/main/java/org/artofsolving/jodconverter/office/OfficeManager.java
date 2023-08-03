//
// JODConverter - Java OpenDocument Converter
// Copyright 2004-2012 Mirko Nasato and contributors
//
// JODConverter is Open Source software, you can redistribute it and/or
// modify it under either (at your option) of the following licenses
//
// 1. The GNU Lesser General Public License v3 (or later)
// -> http://www.gnu.org/licenses/lgpl-3.0.txt
// 2. The Apache License, Version 2.0
// -> http://www.apache.org/licenses/LICENSE-2.0.txt
//
package org.artofsolving.jodconverter.office;

/**
 * An OfficeManager knows how to execute {@link OfficeTask}s.
 * <p>
 * An OfficeManager implementation will typically manage one or more {@link OfficeConnection}s.
 */
/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/30
 */
public interface OfficeManager {

    /**
     * Description:
     * 
     * @param task
     * @throws OfficeException
     */
    void execute(OfficeTask task) throws OfficeException;

    /**
     * Description:
     * 
     * @return
     */
    boolean isRunning();

    /**
     * Description:
     * 
     * @throws OfficeException
     */
    void start() throws OfficeException;

    /**
     * Description:
     * 
     * @throws OfficeException
     */
    void stop() throws OfficeException;
}
