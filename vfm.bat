@echo off
call mvn verify
COPY /Y target\cEnchants-1.0.jar %USERPROFILE%\Documents\testserver\plugins >NUL