package com.hihexo.epp.controller;

import com.hihexo.epp.common.base.BaseController;
import com.hihexo.epp.common.util.Constants;
import com.hihexo.epp.model.BaseParam;
import com.verisign.epp.codec.domain.EPPDomainInfoResp;
import com.verisign.epp.codec.gen.EPPResponse;
import com.verisign.epp.codec.gen.EPPResult;
import com.verisign.epp.codec.gen.EPPUtil;
import com.verisign.epp.codec.rgpext.EPPRgpExtInfData;
import com.verisign.epp.codec.rgpext.EPPRgpExtStatus;
import com.verisign.epp.interfaces.EPPApplicationSingle;
import com.verisign.epp.interfaces.EPPCommandException;
import com.verisign.epp.interfaces.EPPSession;
import com.verisign.epp.namestore.interfaces.NSDomainTst;
import com.verisign.epp.pool.EPPSessionPool;
import com.verisign.epp.util.EPPCatFactory;
import com.verisign.epp.util.TestThread;
import junit.framework.Assert;
import org.apache.log4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;


public class BaseNSController extends BaseController {
    private static org.slf4j.Logger logger = LoggerFactory.getLogger(BaseNSController.class);
    protected HashMap<String,Object> resultMap = new HashMap<String,Object>();
    /**
     * Handle to the Singleton EPP Application instance (
     * <code>EPPApplicationSingle</code>)
     */
    private static EPPApplicationSingle app = EPPApplicationSingle
            .getInstance();

    /** Name of configuration file to use for test (default = epp.config). */
    private static String configFileName = "epp.properties";

    /** Logging category */
    private static final Logger cat = Logger.getLogger(NSDomainTst.class
            .getName(), EPPCatFactory.getInstance().getFactory());

    /** EPP Session pool associated with test */
    private static EPPSessionPool sessionPool = null;

    /**
     * Setup framework for running NSDomainTst tests.
     */
    static {
        String theConfigFileName = System.getProperty("EPP.ConfigFile");
        if (theConfigFileName != null)
            configFileName = theConfigFileName;

        try {
            app.initialize(configFileName);
        }
        catch (EPPCommandException e) {
            e.printStackTrace();
            Assert.fail("Error initializing the EPP Application: " + e);
        }

        // Initialize the session pool
        try {
            sessionPool = EPPSessionPool.getInstance();
            sessionPool.init();
        }
        catch (Exception ex) {
            ex.printStackTrace();
            Assert.fail("Error initializing the session pool: " + ex);
        }
    }

    /**
     * Tear down framework from running NSDomainTst tests.
     */
    protected void tearDown() throws Exception {
        EPPSessionPool.getInstance().close();
    }

    protected String getClientTransId(HttpServletRequest request){
        Object transId = request.getAttribute(Constants.TARNS_ID);
        return null == transId ? ""+System.currentTimeMillis() : ""+transId;
    }

    protected String getAuthString(BaseParam base){
        return base.getAuthStr();
    }

    /**
     * Utility method to borrow a session from the session pool. All exceptions
     * will result in the test failing. This method should only be used for
     * positive session pool tests.
     *
     * @return Session from the session pool
     */
    protected EPPSession borrowSession() {
        EPPSession theSession = null;
        try {
            theSession = sessionPool.borrowObject();
        }
        catch (Exception ex) {
            ex.printStackTrace();
            Assert.fail("borrowSession(): Exception borrowing session: " + ex);
        }

        return theSession;
    }

    /**
     * Utility method to return a session to the session pool. This should be
     * placed in a finally block. All exceptions will result in the test
     * failing.
     *
     * @param aSession
     *            Session to return to the pool
     */
    protected void returnSession(EPPSession aSession) {
        try {
            if (aSession != null)
                sessionPool.returnObject(aSession);
        }
        catch (Exception ex) {
            ex.printStackTrace();
            Assert.fail("returnSession(): Exception returning session: " + ex);
        }
    }

    /**
     * Utility method to invalidate a session in the session pool. This should
     * be placed in an exception block.
     *
     * @param aSession
     *            Session to invalidate in the pool
     */
    protected void invalidateSession(EPPSession aSession) {
        try {
            if (aSession != null)
                sessionPool.invalidateObject(aSession);
        }
        catch (Exception ex) {
            ex.printStackTrace();
            Assert.fail("invalidateSession(): Exception invalidating session: "
                    + ex);
        }
    }

    private void  testEndSession() {
        printStart("testEndSession");

        EPPSession theSession = null;
        try {
            theSession = this.borrowSession();
            sessionPool.invalidateObject(theSession);
            theSession = null;
        }
        catch (Exception ex) {
            ex.printStackTrace();
            org.junit.Assert.fail("testEndSession(): Exception invalidating session: "
                    + ex);
        }
        finally {
            if (theSession != null)
                this.returnSession(theSession);
        }

        printEnd("testEndSession");
    }

    /**
     * Handle a response by printing out the result details.
     *
     * @param aResponse
     *            the response to handle
     */
    protected void handleResponse(EPPResponse aResponse) {

        for (int i = 0; i < aResponse.getResults().size(); i++) {
            EPPResult theResult = (EPPResult) aResponse.getResults().elementAt(
                    i);

            logger.debug("Result Code    : " + theResult.getCode());
            logger.debug("Result Message : " + theResult.getMessage());
            logger.debug("Result Lang    : " + theResult.getLang());

            if (theResult.isSuccess()) {
                logger.debug("Command Passed ");
            }
            else {
                logger.debug("Command Failed ");
            }

            if (theResult.getAllValues() != null) {
                for (int k = 0; k < theResult.getAllValues().size(); k++) {
                    logger.debug("Result Values  : "
                            + theResult.getAllValues().elementAt(k));
                }
            }
        }
    } // End handleResponse(EPPResponse)

    /**
     * Print the start of a test with the <code>Thread</code> name if the
     * current thread is a <code>TestThread</code>.
     *
     * @param aTest
     *            name for the test
     */
    public static void printStart(String aTest) {
        if (Thread.currentThread() instanceof TestThread) {
            logger.debug(Thread.currentThread().getName() + ": ");
            cat.info(Thread.currentThread().getName() + ": " + aTest + " Start");
        }

        logger.debug("Start of " + aTest);
        logger.debug("****************************************************************\n");
    }

    /**
     * Print the end of a test with the <code>Thread</code> name if the current
     * thread is a <code>TestThread</code>.
     *
     * @param aTest
     *            name for the test
     */
    public static void printEnd(String aTest) {
        logger.debug("****************************************************************");

        if (Thread.currentThread() instanceof TestThread) {
            System.out.print(Thread.currentThread().getName() + ": ");
            cat.info(Thread.currentThread().getName() + ": " + aTest + " End");
        }

        logger.debug("End of " + aTest);
        logger.debug("\n");
    }

    public String makeDomainName() {
        return "A_NAME_MUST_FROM_REQUEST_PARAM.com";//FIXME
    }

    /**
     * Makes a unique IP address based off of the current time.
     *
     * @return Unique IP address <code>String</code>
     */
    public String makeIP() {
        return "10.10.10.8"; //FIXME
    }

    /**
     * Makes a unique host name for a domain using the current time.
     *
     * @param newDomainName
     *            DOCUMENT ME!
     *
     * @return Unique host name <code>String</code>
     */
    public String makeHostName(String newDomainName) {
        return new String("lengthof10"+ "." + newDomainName);//FIXME
    }

    /**
     * Makes a unique contact name using the current time.
     *
     * @return Unique contact name <code>String</code>
     */
    public String makeContactName() {
        return new String("Contact Name xx");
    }

    /**
     * This method generates a unique internal Host Name.
     *
     * @return Unique internal host name
     */
    public String makeInternalHost() {
        long tm = System.currentTimeMillis();

        return new String(String.valueOf(tm + "xx").substring(10)
                + "." + this.makeDomainName());
    }

    /**
     * This method generates a unique internal Host Name.
     *
     * @return Unique internal host name
     */
    public String makeExternalHost() {
        long tm = System.currentTimeMillis();

        return new String(String.valueOf(tm + "XX").substring(10)
                + "." + this.makeDomainName());
    }

    /**
     * Inspect the <code>EPPDomainInfoResp</code> and print out the RGP status
     * information contained in the response.
     *
     * @param aResp
     *            Response to inspect
     */
    protected void printRgpStatuses(EPPDomainInfoResp aResp) {
        // Check for the RGP grace period statuses
        if (aResp.hasExtension(EPPRgpExtInfData.class)) {
            EPPRgpExtInfData theRgpInf = (EPPRgpExtInfData) aResp
                    .getExtension(EPPRgpExtInfData.class);
            List rgpStatuses = theRgpInf.getStatuses();
            logger.debug("domainInfo: rgpStatuses.size = "
                    + rgpStatuses.size());
            for (int i = 0; i < rgpStatuses.size(); i++) {
                EPPRgpExtStatus rgpStatus = (EPPRgpExtStatus) rgpStatuses
                        .get(i);

                if (rgpStatus.getStatus().equals(EPPRgpExtStatus.ADD_PERIOD)
                        || rgpStatus.getStatus().equals(
                        EPPRgpExtStatus.AUTO_RENEW_PERIOD)
                        || rgpStatus.getStatus().equals(
                        EPPRgpExtStatus.RENEW_PERIOD)
                        || rgpStatus.getStatus().equals(
                        EPPRgpExtStatus.TRANSFER_PERIOD)) {
                    logger.debug("domainInfo: rgp grace period status "
                            + rgpStatus.getStatus());
                    System.out
                            .println("domainInfo: rgp grace period end date = "
                                    + EPPUtil.decodeTimeInstant(rgpStatus
                                    .getMessage().substring(8)));
                }
                else if (rgpStatus.getStatus().equals(
                        EPPRgpExtStatus.REDEMPTION_PERIOD)
                        || rgpStatus.getStatus().equals(
                        EPPRgpExtStatus.PENDING_RESTORE)
                        || rgpStatus.getStatus().equals(
                        EPPRgpExtStatus.PENDING_DELETE)) {
                    logger.debug("domainInfo: rgp pending period status "
                            + rgpStatus.getStatus());

                }

            }
        }

    }
}
