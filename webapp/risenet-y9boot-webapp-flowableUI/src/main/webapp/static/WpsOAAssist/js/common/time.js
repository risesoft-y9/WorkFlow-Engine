var MAX_DATE = 2958465    // about year 9999
var MAX_DATE_1904 = 2957003

// Half a second, expressed in days
var HALF_SECOND = (1.0 / 172800.0)

// Half a millisecond
var HALF_MILLISECOND = (1.0 / 2000.0)

// 从 0000/1/1 到 1900/1/1 的时间序数值
var SERIAL_BASE_NUM = 693959

// 从 0000/1/1 到 1904/1/1 的时间序数值
var SERIAL_BASE_NUM_1904 = 695421

// 指示是否假定 1900 年为润年(兼容 Excel)
var YEAR1900_ISLEAP = 0

// One-based array of days in year at month start
var _afxMonthDays =
    [0, 31, 59, 90, 120, 151, 181, 212, 243, 273, 304, 334, 365];
var b1904 = false;
var bDisable1900Year = false;

function toTime(dtSrc, b1904) {
    var tmDest = {};
    var nDays = 0;             // Number of days since Dec. 30, 1899
    var nDaysAbsolute = 0;     // Number of days since 1/1/0
    var nSecsInDay = 0;        // Time in seconds since midnight
    var nMinutesInDay = 0;     // Minutes in day

    var n400Years = 0;         // Number of 400 year increments since 1/1/0
    var n400Century = 0;       // Century within 400 year block (0,1,2 or 3)
    var n4Years = 0;           // Number of 4 year increments since 1/1/0
    var n4Day = 0;             // Day within 4 year block
    //  (0 is 1/1/yr1, 1460 is 12/31/yr4)
    var n4Yr = 0;              // Year within 4 year block (0,1,2 or 3)
    var bLeap4 = true;     // true if 4 year block includes leap year
    var bIs1900Year = false; // true if this year is 1900

    var dblDate = dtSrc; // tempory serial date

    // Round to the second
    if (0 == 0)
        dblDate += ((dtSrc > 0.0) ? HALF_SECOND : -HALF_SECOND);

    // If a valid date, then this conversion should not overflow
    nDays = Math.floor(dblDate);

    nDaysAbsolute = Math.floor(dblDate) + (b1904 ? SERIAL_BASE_NUM_1904 : SERIAL_BASE_NUM); // Add days from 1/1/0 to 12/30/1899

    dblDate = Math.abs(dblDate);
    var dblSecsInDay = (dblDate - Math.floor(dblDate)) * 86400.0;

    nSecsInDay = Math.floor(dblSecsInDay);

    // modified by tsingbo:
    // Calculate the day of week (sun=0, mon=1...)
    //   -1 because 1/1/0 is Sat.
    tmDest.tm_wday = Math.floor((nDaysAbsolute - 1) % 7);

    if (!bDisable1900Year && !b1904 &&
        nDaysAbsolute >= SERIAL_BASE_NUM &&
        nDaysAbsolute < SERIAL_BASE_NUM + 367) {
        bIs1900Year = true;
        n4Day = nDaysAbsolute - SERIAL_BASE_NUM;
        bLeap4 = true;
        n400Years = 4;
        n400Century = 3;
        n4Yr = 0;
    }
    else {
        // Leap years every 4 yrs except centuries not multiples of 400.
        n400Years = Math.floor(nDaysAbsolute / 146097);

        // Set nDaysAbsolute to day within 400-year block
        nDaysAbsolute %= 146097;

        // -1 because first century has extra day
        n400Century = Math.floor((nDaysAbsolute - 1) / 36524);

        // Non-leap century
        if (n400Century != 0) {
            // Set nDaysAbsolute to day within century
            nDaysAbsolute = (nDaysAbsolute - 1) % 36524;

            // +1 because 1st 4 year increment has 1460 days
            n4Years = Math.floor((nDaysAbsolute + 1) / 1461);

            if (n4Years != 0)
                n4Day = Math.floor((nDaysAbsolute + 1) % 1461);
            else {
                bLeap4 = false;
                n4Day = Math.floornDaysAbsolute;
            }
        }
        else {
            // Leap century - not special case!
            n4Years = Math.floor(nDaysAbsolute / 1461);
            n4Day = Math.floor(nDaysAbsolute % 1461);
        }

        if (bLeap4) {
            // -1 because first year has 366 days
            n4Yr = (n4Day - 1) / 365;

            if (n4Yr != 0)
                n4Day = (n4Day - 1) % 365;
        }
        else {
            n4Yr = n4Day / 365;
            n4Day %= 365;
        }
    }

    // n4Day is now 0-based day of year. Save 1-based day of year, year number
    tmDest.tm_yday = Math.floor(n4Day) + 1;
    tmDest.tm_year = Math.floor(n400Years * 400 + n400Century * 100 + n4Years * 4 + n4Yr);

    // Handle leap year: before, on, and after Feb. 29.
    if (n4Yr == 0 && bLeap4) {
        // Leap Year
        if ((!bIs1900Year && n4Day == 59) ||
            (bIs1900Year && n4Day == 60)) {
            /* Feb. 29 */
            tmDest.tm_mon = 2;
            tmDest.tm_mday = 29;
        }
        else {
            // Pretend it's not a leap year for month/day comp.
            if (n4Day >= 60)
                --n4Day;
        }
    } else {
        // Make n4DaY a 1-based day of non-leap year and compute
        //  month/day for everything but Feb. 29.
        if (!bIs1900Year)
            ++n4Day;

        // Month number always >= n/32, so save some loop time */
        for (tmDest.tm_mon = (n4Day >> 5) + 1;
            n4Day > _afxMonthDays[tmDest.tm_mon]; tmDest.tm_mon++);

        tmDest.tm_mday = Math.floor(n4Day - _afxMonthDays[tmDest.tm_mon - 1]);
    }

    if (nSecsInDay == 0)
        tmDest.tm_hour = tmDest.tm_min = tmDest.tm_sec = 0;
    else {
        tmDest.tm_sec = Math.floor(nSecsInDay % 60);
        nMinutesInDay = nSecsInDay / 60;
        tmDest.tm_min = Math.floor(nMinutesInDay % 60);
        tmDest.tm_hour = Math.floor(nMinutesInDay / 60);
    }
    
    return new Date(tmDest.tm_year, tmDest.tm_mon + 1, tmDest.tm_mday, tmDest.tm_mday, tmDest.tm_min, tmDest.tm_sec);
}
