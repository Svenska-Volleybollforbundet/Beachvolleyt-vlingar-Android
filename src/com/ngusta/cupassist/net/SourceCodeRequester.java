package com.ngusta.cupassist.net;

import java.io.IOException;

public interface SourceCodeRequester {

    public String getSourceCode(String url) throws IOException;
}
