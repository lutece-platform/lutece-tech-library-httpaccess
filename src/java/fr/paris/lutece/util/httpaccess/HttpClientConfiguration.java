package fr.paris.lutece.util.httpaccess;

public class HttpClientConfiguration {


    /** The _str proxy host. */
    private String _strProxyHost;

    /** The _str proxy port. */
    private String _strProxyPort;

    /** The _str proxy user name. */
    private String _strProxyUserName;

    /** The _str proxy password. */
    private String _strProxyPassword;

    /** The _str host name. */
    private String _strHostName;

    /** The _str domain name. */
    private String _strDomainName;

    /** The _str realm. */
    private String _strRealm;

    /** The _str no proxy for. */
    private String _strNoProxyFor;

    /** The _str content charset. */
    private String _strContentCharset;

    /** The _str element charset. */
    private String _strElementCharset;

    /** The _str socket timeout. */
    private String _strSocketTimeout;

    /** The _str connection timeout. */
    private String _strConnectionTimeout;

    /** The _b connection pool enabled. */
    private boolean _bConnectionPoolEnabled;

    /** The _str connection pool max total connection. */
    private String _strConnectionPoolMaxTotalConnection;

    /** The _str connection pool max connection per host. */
    private String _strConnectionPoolMaxConnectionPerHost;

	public String getProxyHost() {
		return _strProxyHost;
	}

	public void setProxyHost(String _strProxyHost) {
		this._strProxyHost = _strProxyHost;
	}

	public String getProxyPort() {
		return _strProxyPort;
	}

	public void setProxyPort(String _strProxyPort) {
		this._strProxyPort = _strProxyPort;
	}

	public String getProxyUserName() {
		return _strProxyUserName;
	}

	public void setProxyUserName(String _strProxyUserName) {
		this._strProxyUserName = _strProxyUserName;
	}

	public String getProxyPassword() {
		return _strProxyPassword;
	}

	public void setProxyPassword(String _strProxyPassword) {
		this._strProxyPassword = _strProxyPassword;
	}

	public String getHostName() {
		return _strHostName;
	}

	public void setHostName(String _strHostName) {
		this._strHostName = _strHostName;
	}

	public String getDomainName() {
		return _strDomainName;
	}

	public void setDomainName(String _strDomainName) {
		this._strDomainName = _strDomainName;
	}

	public String getRealm() {
		return _strRealm;
	}

	public void setRealm(String _strRealm) {
		this._strRealm = _strRealm;
	}

	public String getNoProxyFor() {
		return _strNoProxyFor;
	}

	public void setNoProxyFor(String _strNoProxyFor) {
		this._strNoProxyFor = _strNoProxyFor;
	}

	public String getContentCharset() {
		return _strContentCharset;
	}

	public void setContentCharset(String _strContentCharset) {
		this._strContentCharset = _strContentCharset;
	}

	public String getElementCharset() {
		return _strElementCharset;
	}

	public void setElementCharset(String _strElementCharset) {
		this._strElementCharset = _strElementCharset;
	}

	public String getSocketTimeout() {
		return _strSocketTimeout;
	}

	public void setSocketTimeout(String _strSocketTimeout) {
		this._strSocketTimeout = _strSocketTimeout;
	}

	public String getConnectionTimeout() {
		return _strConnectionTimeout;
	}

	public void setConnectionTimeout(String _strConnectionTimeout) {
		this._strConnectionTimeout = _strConnectionTimeout;
	}

	public boolean isConnectionPoolEnabled() {
		return _bConnectionPoolEnabled;
	}

	public void setConnectionPoolEnabled(boolean _bConnectionPoolEnabled) {
		this._bConnectionPoolEnabled = _bConnectionPoolEnabled;
	}

	public String getConnectionPoolMaxTotalConnection() {
		return _strConnectionPoolMaxTotalConnection;
	}

	public void setConnectionPoolMaxTotalConnection(String _strConnectionPoolMaxTotalConnection) {
		this._strConnectionPoolMaxTotalConnection = _strConnectionPoolMaxTotalConnection;
	}

	public String getConnectionPoolMaxConnectionPerHost() {
		return _strConnectionPoolMaxConnectionPerHost;
	}

	public void setConnectionPoolMaxConnectionPerHost(String _strConnectionPoolMaxConnectionPerHost) {
		this._strConnectionPoolMaxConnectionPerHost = _strConnectionPoolMaxConnectionPerHost;
	}

}
