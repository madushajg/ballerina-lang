/*
 * Copyright (c) 2019, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.ballerinalang.packerina.cmd;

import org.ballerinalang.compiler.BLangCompilerException;
import org.ballerinalang.tool.BLauncherCmd;
import org.ballerinalang.tool.BLauncherException;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;


/**
 * Command tests supper class.
 *
 * @since 1.0.0
 */
public abstract class CommandTest {
    protected Path tmpDir;
    private ByteArrayOutputStream console;
    protected PrintStream printStream;
    
    @BeforeClass
    public void setup() throws IOException {
        this.tmpDir = Files.createTempDirectory("b7a-cmd-test");
        this.console = new ByteArrayOutputStream();
        this.printStream = new PrintStream(this.console);
    }

    protected String readOutput() throws IOException {
        return readOutput(false);
    }

    protected String readOutput(boolean slient) throws IOException {
        String output = "";
        output = console.toString();
        console.close();
        console = new ByteArrayOutputStream();
        printStream = new PrintStream(console);
        if (!slient) {
            PrintStream out = System.out;
            out.println(output);
        }
        return output;
    }
    
    /**
     * Execute a command and get the exception.
     *
     * @param cmd The command.
     * @return The error message.
     */
    public String executeAndGetException(BLauncherCmd cmd) {
        try {
            cmd.execute();
            Assert.fail("Expected exception did not occur.");
        } catch (BLauncherException e) {
            if (e.getMessages().size() == 1) {
                return e.getMessages().get(0);
            }
        } catch (BLangCompilerException e) {
            return e.getMessage();
        } catch (Exception e) {
            Assert.fail("Invalid exception found: " + e.getClass().toString() + "-" + e.getMessage());
        }
        return null;
    }

    @AfterClass
    public void cleanup() throws IOException {
        Files.walk(this.tmpDir)
                .sorted(Comparator.reverseOrder())
                .forEach(path -> {
                    try {
                        Files.delete(path);
                    } catch (IOException e) {
                        Assert.fail(e.getMessage(), e);
                    }
                });
        console.close();
        printStream.close();
    }
}
