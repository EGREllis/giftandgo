# giftandgo

It works but...

Known bugs:
1) Servlet thread hangs if all lines do not end with new-line (\n) - because it uses BufferedReader which blocks.

Known features (of debatable worth):
1) If any line submitted is inaccurate, Exceptions are thrown and the whole batch is aborted.
