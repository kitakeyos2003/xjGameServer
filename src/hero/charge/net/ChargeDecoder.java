// 
// Decompiled by Procyon v0.5.36
// 
package hero.charge.net;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import java.nio.charset.Charset;
import java.util.regex.Pattern;
import java.nio.charset.CharsetDecoder;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;

public class ChargeDecoder extends CumulativeProtocolDecoder {

    private static final CharsetDecoder decoder;
    private static final Pattern PATTERN;
    private static final String PARAMETER_CONNECT_CHAR = "&";
    private static final String PARAMETER_EVALUATE_CHAR = "=";

    static {
        decoder = Charset.forName("ISO-8859-1").newDecoder();
        PATTERN = Pattern.compile(" ");
    }

    protected boolean doDecode(final IoSession _session, final IoBuffer _buffer, final ProtocolDecoderOutput _out) throws Exception {
        try {
            String request = _buffer.getString(ChargeDecoder.decoder);
            HashMap<String, String> param = parseHttpRequest(request);
            _out.write((Object) param);
            _out.flush();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    private static HashMap<String, String> parseHttpRequest(final String _requestContent) {
        HttpRequest request = new HttpRequest();
        try {
            int start;
            int end;
            if (-1 != (start = _requestContent.indexOf(" /")) && -1 != (end = _requestContent.indexOf("HTTP/"))) {
                String coreRequest = _requestContent.substring(start + 2, end).trim();
                int reqIndex = coreRequest.indexOf("?");
                if (reqIndex < 0) {
                    request.addParam("REQ_TYPE", coreRequest);
                } else {
                    request.addParam("REQ_TYPE", coreRequest.substring(0, reqIndex));
                }
            }
            int index = _requestContent.indexOf("\r\n\r\n");
            if (index > 0) {
                String[] headers = _requestContent.substring(0, index).split("\r\n");
                parseHeaders(request, headers);
                String contentLen = request.getParam("Content-Length");
                if (contentLen != null) {
                    int len = Integer.parseInt(contentLen);
                    String content = _requestContent.substring(_requestContent.length() - len);
                    request.setContent(content);
                    if (request.getRequestMethod().equals("POST")) {
                        parseParam(request, new String(request.getContent().getBytes(), "ISO-8859-1"));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return request.getParamsMap();
    }

    private static void parseHeaders(final HttpRequest request, final String[] headers) {
        String[] s = ChargeDecoder.PATTERN.split(headers[0]);
        request.setRequestMethod(s[0]);
        request.setRequestURI(s[1]);
        parseURI(request, s[1]);
        for (int i = 1; i < headers.length; ++i) {
            int index = headers[i].indexOf(":");
            request.addParam(headers[i].substring(0, index).trim(), headers[i].substring(index + 1).trim());
        }
    }

    private static void parseURI(final HttpRequest _request, final String _requestContent) {
        String coreRequest = _requestContent;
        int reqIndex = coreRequest.indexOf("?");
        if (reqIndex < 0) {
            _request.setBriefRequestURI(coreRequest);
        } else {
            String reqType = coreRequest.substring(0, reqIndex);
            _request.setBriefRequestURI(reqType);
            coreRequest = coreRequest.substring(reqIndex + 1);
            parseParam(_request, coreRequest);
        }
    }

    private static void parseParam(final HttpRequest _request, final String _requestContent) {
        String[] parameters = _requestContent.split("&");
        String[] array;
        for (int length = (array = parameters).length, i = 0; i < length; ++i) {
            String parameterExprion = array[i];
            int evaluateCharIndex = parameterExprion.indexOf("=");
            if (-1 != evaluateCharIndex) {
                String parameterName = parameterExprion.substring(0, evaluateCharIndex).trim();
                String parameterValue = parameterExprion.substring(evaluateCharIndex + 1).trim();
                try {
                    parameterValue = URLDecoder.decode(parameterValue, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                _request.setURIParam(parameterName, parameterValue);
            }
        }
    }
}
