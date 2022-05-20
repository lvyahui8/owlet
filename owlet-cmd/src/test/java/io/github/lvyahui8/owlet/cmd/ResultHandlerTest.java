package io.github.lvyahui8.owlet.cmd;

import junit.framework.TestCase;

public class ResultHandlerTest extends TestCase {

    public void testOutput() throws Exception {
        ResultHandler handler = new ResultHandler(null,null,null);
        handler.output();
    }
}