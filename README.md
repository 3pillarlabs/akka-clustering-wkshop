
# Akka clustering workshop

## Initialize the repo

```
git clone git@github.com:3pillarlabs/akka-clustering-wkshop.git
```

```
git fetch
```

## Jump to steps in workshop's code

```
git checkout -b step1b step1
```

Step two:

```
git checkout -b step2b step2
```

... and so on to *step4*


## SBT How-to

Run a specific main class:

```
$ sbt
> run-main com.example.ApplicationMain
[info] Running com.example.ApplicationMain 
```

Run a specific test:

```
$ sbt
> test:test-only *PingPongActorSpec
[INFO] [11/13/2015 11:14:12.701] [pool-2-thread-1] [akka.remote.Remoting] Starting remoting
```
