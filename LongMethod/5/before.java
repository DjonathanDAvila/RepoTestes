public static SMSParsedResult parse(Result result) {
    String rawText = result.getText();
    if (rawText == null) {
      return null;
    }
    int prefixLength;
    if (rawText.startsWith("sms:") || rawText.startsWith("SMS:") ||
        rawText.startsWith("mms:") || rawText.startsWith("MMS:")) {
      prefixLength = 4;
    } else if (rawText.startsWith("smsto:") || rawText.startsWith("SMSTO:") ||
               rawText.startsWith("mmsto:") || rawText.startsWith("MMSTO:")) {
      prefixLength = 6;
    } else {
      return null;
    }

    // Check up front if this is a URI syntax string with query arguments
    Hashtable nameValuePairs = parseNameValuePairs(rawText);
    String subject = null;
    String body = null;
    boolean querySyntax = false;
    if (nameValuePairs != null && !nameValuePairs.isEmpty()) {
      subject = (String) nameValuePairs.get("subject");
      body = (String) nameValuePairs.get("body");
      querySyntax = true;
    }

    // Drop sms, query portion
    int queryStart = rawText.indexOf('?', prefixLength);
    String smsURIWithoutQuery;
    // If it's not query syntax, the question mark is part of the subject or message
    if (queryStart < 0 || !querySyntax) {
      smsURIWithoutQuery = rawText.substring(prefixLength);
    } else {
      smsURIWithoutQuery = rawText.substring(prefixLength, queryStart);
    }
    int numberEnd = smsURIWithoutQuery.indexOf(';');
    String number;
    String via;
    if (numberEnd < 0) {
      number = smsURIWithoutQuery;
      via = null;
    } else {
      number = smsURIWithoutQuery.substring(0, numberEnd);
      String maybeVia = smsURIWithoutQuery.substring(numberEnd + 1);
      if (maybeVia.startsWith("via=")) {
        via = maybeVia.substring(4);
      } else {
        via = null;
      }
    }

    // Thanks to dominik.wild for suggesting this enhancement to support
    // smsto:number:body URIs
    if (body == null) {
      int bodyStart = number.indexOf(':');
      if (bodyStart >= 0) {
        body = number.substring(bodyStart + 1);
        number = number.substring(0, bodyStart);
      }
    }
    return new SMSParsedResult("sms:" + number, number, via, subject, body, null);
  }
