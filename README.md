# giftandgo

Hi,

To execute run the ./iterate.sh script (assumes you have maven and docker installed and that docker is running).

To test use either ./test.sh (for your own ip) or ./testCanada.sh using the IP address provided in the ip-api.com documentation.

Data processing is fully implmented.
Validation with feature flag is fully implemented.
Database features not implemented - ran into some issues creating the schema automatically first with JPA then with Flyay.

Known bugs:
1) Servlet thread hangs if all lines do not end with new-line (\n) - because it uses BufferedReader which blocks.
This could by fixed by using new String(data).split("\n") instread of the BufferedReader.

Known features (of debatable worth):
1) If any line submitted is inaccurate, Exceptions are thrown and the whole batch is aborted.
