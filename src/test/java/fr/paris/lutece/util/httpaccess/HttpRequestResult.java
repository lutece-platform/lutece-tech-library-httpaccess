package fr.paris.lutece.util.httpaccess;

public class HttpRequestResult
{

    public HttpRequestResult( )
    {

    }

    public HttpRequestResult( String _strMethodName, String [ ] headers, String requestBody, String requestPath )
    {
        super( );
        this._strMethodName = _strMethodName;
        this.headers = headers;
        this.requestBody = requestBody;
        this.requestPath = requestPath;
    }

    private String _strMethodName;
    private String [ ] headers;
    private String requestBody;
    private String requestPath;

    public String [ ] getHeaders( )
    {
        return headers;
    }

    public void setHeaders( String [ ] headers )
    {
        this.headers = headers;
    }

    public String getMethodName( )
    {
        return _strMethodName;
    }

    public void setMethodName( String strMethodName )
    {
        this._strMethodName = strMethodName;
    }

    public String getRequestBody( )
    {
        return requestBody;
    }

    public void setRequestBody( String requestBody )
    {
        this.requestBody = requestBody;
    }

    public String getRequestPath( )
    {
        return requestPath;
    }

    public void setRequestPath( String requestPath )
    {
        this.requestPath = requestPath;
    }

}
