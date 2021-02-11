@echo off
set makefile=makefile.gen
set sgdk_path=
set sgdk_unix_path=
set backupPath=%path%

:loop
if not exist %sgdk_path%\%makefile% (
  set sgdk_path=..\%sgdk_path%
  set sgdk_unix_path=../%sgdk_unix_path%
  goto :loop
) else (
  path=%sgdk_path%\bin
  %sgdk_path%\bin\make.exe -f %sgdk_unix_path%/%makefile%
)

path=%backupPath%