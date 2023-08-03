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

import java.net.ConnectException;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.sun.star.frame.XDesktop;
import com.sun.star.lang.DisposedException;

class ManagedOfficeProcess {

    private static final Integer EXIT_CODE_NEW_INSTALLATION = Integer.valueOf(81);

    private final ManagedOfficeProcessSettings settings;

    private final OfficeProcess process;
    private final OfficeConnection connection;

    ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(5, 20, 0L, TimeUnit.MILLISECONDS,
        new LinkedBlockingQueue<Runnable>(), new NamedThreadFactory("OfficeProcessThread"));

    private final Logger logger = Logger.getLogger(getClass().getName());

    public ManagedOfficeProcess(ManagedOfficeProcessSettings settings) throws OfficeException {
        this.settings = settings;
        process = new OfficeProcess(settings.getOfficeHome(), settings.getUnoUrl(), settings.getRunAsArgs(),
            settings.getTemplateProfileDir(), settings.getWorkDir(), settings.getProcessManager());
        connection = new OfficeConnection(settings.getUnoUrl());
    }

    private void doEnsureProcessExited() throws OfficeException {
        try {
            int exitCode = process.getExitCode(settings.getRetryInterval(), settings.getRetryTimeout());
            logger.info("process exited with code " + exitCode);
        } catch (RetryTimeoutException retryTimeoutException) {
            doTerminateProcess();
        }
        process.deleteProfileDir();
    }

    private void doStartProcessAndConnect() throws OfficeException {
        try {
            process.start();
            new AbstractRetryable() {
                @Override
                protected void attempt() throws TemporaryException, Exception {
                    try {
                        connection.connect();
                    } catch (ConnectException connectException) {
                        Integer exitCode = process.getExitCode();
                        if (exitCode == null) {
                            // process is running; retry later
                            throw new TemporaryException(connectException);
                        } else if (EXIT_CODE_NEW_INSTALLATION.equals(exitCode)) {
                            // restart and retry later
                            // see http://code.google.com/p/jodconverter/issues/detail?id=84
                            logger.log(Level.WARNING, "office process died with exit code 81; restarting it");
                            process.start(true);
                            throw new TemporaryException(connectException);
                        } else {
                            throw new OfficeException("office process died with exit code " + exitCode);
                        }
                    }
                }
            }.execute(settings.getRetryInterval(), settings.getRetryTimeout());
        } catch (Exception exception) {
            throw new OfficeException("could not establish connection", exception);
        }
    }

    private void doStopProcess() {
        try {
            XDesktop desktop = OfficeUtils.cast(XDesktop.class, connection.getService(OfficeUtils.SERVICE_DESKTOP));
            desktop.terminate();
        } catch (DisposedException disposedException) {
            // expected
        } catch (Exception exception) {
            // in case we can't get hold of the desktop
            doTerminateProcess();
        }
        doEnsureProcessExited();
    }

    private void doTerminateProcess() throws OfficeException {
        try {
            int exitCode = process.forciblyTerminate(settings.getRetryInterval(), settings.getRetryTimeout());
            logger.info("process forcibly terminated with code " + exitCode);
        } catch (Exception exception) {
            throw new OfficeException("could not terminate process", exception);
        }
    }

    public OfficeConnection getConnection() {
        return connection;
    }

    boolean isConnected() {
        return connection.isConnected();
    }

    public void restartAndWait() {
        Future<?> future = threadPoolExecutor.submit(new Runnable() {
            @Override
            public void run() {
                doStopProcess();
                doStartProcessAndConnect();
            }
        });
        try {
            future.get();
        } catch (Exception exception) {
            throw new OfficeException("failed to restart", exception);
        }
    }

    public void restartDueToLostConnection() {
        threadPoolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    doEnsureProcessExited();
                    doStartProcessAndConnect();
                } catch (OfficeException officeException) {
                    logger.log(Level.SEVERE, "could not restart process", officeException);
                }
            }
        });
    }

    public void restartDueToTaskTimeout() {
        threadPoolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                doTerminateProcess();
                // will cause unexpected disconnection and subsequent restart
            }
        });
    }

    public void startAndWait() throws OfficeException {
        Future<?> future = threadPoolExecutor.submit(new Runnable() {
            @Override
            public void run() {
                doStartProcessAndConnect();
            }
        });
        try {
            future.get();
        } catch (Exception exception) {
            throw new OfficeException("failed to start and connect", exception);
        }
    }

    public void stopAndWait() throws OfficeException {
        Future<?> future = threadPoolExecutor.submit(new Runnable() {
            @Override
            public void run() {
                doStopProcess();
            }
        });
        try {
            future.get();
        } catch (Exception exception) {
            throw new OfficeException("failed to start and connect", exception);
        }
    }

}
