# giftandgo

Hi,

To execute run the ./iterate.sh script (assumes you have maven and docker installed and that docker is running).
To test use either ./test.sh (for your own ip) or ./testCanada.sh using the IP address provided in the ip-api.com documentation.

Data processing is fully implemented and tested automatically.
Validation with feature flag is fully implemented and tested automatically.
Database functionality implemented but no integration tests.

Known features/issues:
1) If any line submitted is inaccurate, Exceptions are thrown and the whole batch is aborted.

Improvements if I spent more time:
1) The iterate.sh script does not exit early if maven fails to build - could be fixed by checking $?.
2) The first request takes longer than any other.  Could warm itself by making requests at application start.

I made a concurrent version (see the "concurrent" branch).

Had some fun, would appreciate feedback.

Warm regards,
Edward
