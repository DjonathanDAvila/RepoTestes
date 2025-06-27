static int lookAheadTest(CharSequence msg, int startpos, int currentMode) {
    if (startpos >= msg.length()) {
      return currentMode;
    }
    float[] charCounts;
    //step J
    if (currentMode == ASCII_ENCODATION) {
      charCounts = new float[]{0, 1, 1, 1, 1, 1.25f};
    } else {
      charCounts = new float[]{1, 2, 2, 2, 2, 2.25f};
      charCounts[currentMode] = 0;
    }

    int charsProcessed = 0;
    while (true) {
      //step K
      if ((startpos + charsProcessed) == msg.length()) {
        int min = Integer.MAX_VALUE;
        byte[] mins = new byte[6];
        int[] intCharCounts = new int[6];
        min = findMinimums(charCounts, intCharCounts, min, mins);
        int minCount = getMinimumCount(mins);

        if (intCharCounts[ASCII_ENCODATION] == min) {
          return ASCII_ENCODATION;
        }
        if (minCount == 1 && mins[BASE256_ENCODATION] > 0) {
          return BASE256_ENCODATION;
        }
        if (minCount == 1 && mins[EDIFACT_ENCODATION] > 0) {
          return EDIFACT_ENCODATION;
        }
        if (minCount == 1 && mins[TEXT_ENCODATION] > 0) {
          return TEXT_ENCODATION;
        }
        if (minCount == 1 && mins[X12_ENCODATION] > 0) {
          return X12_ENCODATION;
        }
        return C40_ENCODATION;
      }

      char c = msg.charAt(startpos + charsProcessed);
      charsProcessed++;

      //step L
      if (isDigit(c)) {
        charCounts[ASCII_ENCODATION] += 0.5f;
      } else if (isExtendedASCII(c)) {
        charCounts[ASCII_ENCODATION] = (float) Math.ceil(charCounts[ASCII_ENCODATION]);
        charCounts[ASCII_ENCODATION] += 2.0f;
      } else {
        charCounts[ASCII_ENCODATION] = (float) Math.ceil(charCounts[ASCII_ENCODATION]);
        charCounts[ASCII_ENCODATION]++;
      }

      //step M
      if (isNativeC40(c)) {
        charCounts[C40_ENCODATION] += 2.0f / 3.0f;
      } else if (isExtendedASCII(c)) {
        charCounts[C40_ENCODATION] += 8.0f / 3.0f;
      } else {
        charCounts[C40_ENCODATION] += 4.0f / 3.0f;
      }

      //step N
      if (isNativeText(c)) {
        charCounts[TEXT_ENCODATION] += 2.0f / 3.0f;
      } else if (isExtendedASCII(c)) {
        charCounts[TEXT_ENCODATION] += 8.0f / 3.0f;
      } else {
        charCounts[TEXT_ENCODATION] += 4.0f / 3.0f;
      }

      //step O
      if (isNativeX12(c)) {
        charCounts[X12_ENCODATION] += 2.0f / 3.0f;
      } else if (isExtendedASCII(c)) {
        charCounts[X12_ENCODATION] += 13.0f / 3.0f;
      } else {
        charCounts[X12_ENCODATION] += 10.0f / 3.0f;
      }

      //step P
      if (isNativeEDIFACT(c)) {
        charCounts[EDIFACT_ENCODATION] += 3.0f / 4.0f;
      } else if (isExtendedASCII(c)) {
        charCounts[EDIFACT_ENCODATION] += 17.0f / 4.0f;
      } else {
        charCounts[EDIFACT_ENCODATION] += 13.0f / 4.0f;
      }

      // step Q
      if (isSpecialB256(c)) {
        charCounts[BASE256_ENCODATION] += 4.0f;
      } else {
        charCounts[BASE256_ENCODATION]++;
      }

      //step R
      if (charsProcessed >= 4) {
        int[] intCharCounts = new int[6];
        byte[] mins = new byte[6];
        findMinimums(charCounts, intCharCounts, Integer.MAX_VALUE, mins);

        if (intCharCounts[ASCII_ENCODATION] < min(intCharCounts[BASE256_ENCODATION],
              intCharCounts[C40_ENCODATION], intCharCounts[TEXT_ENCODATION], intCharCounts[X12_ENCODATION],
              intCharCounts[EDIFACT_ENCODATION])) {
          return ASCII_ENCODATION;
        }
        if (intCharCounts[BASE256_ENCODATION] < intCharCounts[ASCII_ENCODATION] ||
              intCharCounts[BASE256_ENCODATION] + 1 < min(intCharCounts[C40_ENCODATION],
              intCharCounts[TEXT_ENCODATION], intCharCounts[X12_ENCODATION], intCharCounts[EDIFACT_ENCODATION])) {
          return BASE256_ENCODATION;
        }
        if (intCharCounts[EDIFACT_ENCODATION] + 1 < min(intCharCounts[BASE256_ENCODATION],
              intCharCounts[C40_ENCODATION] , intCharCounts[TEXT_ENCODATION] , intCharCounts[X12_ENCODATION],
              intCharCounts[ASCII_ENCODATION])) {
          return EDIFACT_ENCODATION;
        }
        if (intCharCounts[TEXT_ENCODATION] + 1 < min(intCharCounts[BASE256_ENCODATION],
              intCharCounts[C40_ENCODATION] , intCharCounts[EDIFACT_ENCODATION] , intCharCounts[X12_ENCODATION],
              intCharCounts[ASCII_ENCODATION])) {
          return TEXT_ENCODATION;
        }
        if (intCharCounts[X12_ENCODATION] + 1 < min(intCharCounts[BASE256_ENCODATION],
              intCharCounts[C40_ENCODATION] , intCharCounts[EDIFACT_ENCODATION] , intCharCounts[TEXT_ENCODATION],
              intCharCounts[ASCII_ENCODATION])) {
          return X12_ENCODATION;
        }
        if (intCharCounts[C40_ENCODATION] + 1 < min(intCharCounts[ASCII_ENCODATION],
              intCharCounts[BASE256_ENCODATION] , intCharCounts[EDIFACT_ENCODATION] , intCharCounts[TEXT_ENCODATION])) {
          if (intCharCounts[C40_ENCODATION] < intCharCounts[X12_ENCODATION]) {
            return C40_ENCODATION;
          }
          if (intCharCounts[C40_ENCODATION] == intCharCounts[X12_ENCODATION]) {
            int p = startpos + charsProcessed + 1;
            while (p < msg.length()) {
              char tc = msg.charAt(p);
              if (isX12TermSep(tc)) {
                return X12_ENCODATION;
              }
              if (!isNativeX12(tc)) {
                break;
              }
              p++;
            }
            return C40_ENCODATION;
          }
        }
      }
    }
  }
