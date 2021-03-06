[![License](http://img.shields.io/:license-mit-blue.svg)](https://github.com/roosta/buccaneer/blob/master/LICENSE.md)

# Buccaneer

<!-- ![](https://raw.githubusercontent.com/Gonzih/cljs-electron/master/demo.gif) -->

Ripped media data viewer built using [ClojureScript](https://clojurescript.org/) and [Electron](https://electronjs.org/)

## Features
* View media information from downloaded media (movie, tv-show)
* Expects a single folder containing media, no sorting required
* Uses [themoviedb](https://www.themoviedb.org/), and
  [OMDb](https://www.omdbapi.com/) to fetch information
* KISS, keeping it simple, this about all that Buccaneer does

## Download
Go to [releases](https://github.com/roosta/buccaneer/releases), pick
the latest release and under `assets` is prebuilt binaries for windows
and linux. Download and run.

## Usage
Pass a directory containing `proper` downloaded media, and Buccaneer
will try to parse filenames and get relevant data for each entry,
presented as a sidebar of media and a content section for displaying
fetched data.

## Configuration

Edit
[config.example.edn](https://github.com/roosta/buccaneer/blob/master/config.example.edn)
and add your [themoviedb
API](https://www.themoviedb.org/documentation/api) key and your [OMDb
API](https://www.omdbapi.com/) key to their respective places:

``` clojure
{:moviedb-api-key "Your themoviedb.org API key"
 :omdb-api-key "Your omdbapi.com API key"}
```
Save as config.edn in root folder

## Running it

```shell
npm install electron -g          # install electron binaries

lein cooper                      # compile cljs and start figwheel
electron .                       # start electron from another terminal
```

## Releasing

```shell
lein do clean, cljsbuild once frontend-release, cljsbuild once electron-release
electron . # start electron to test that everything works
```

After that you can follow [distribution guide for the
electron.](https://github.com/atom/electron/blob/master/docs/tutorial/application-distribution.md)

The easiest way to package an electron app is by using [electron-packager](https://github.com/maxogden/electron-packager):

```shell
npm install electron-packager -g                                       # install electron packager
electron-packager . Buccaneer --platform=darwin --arch=x64 --electron-version=6.0.12 # package it!
```
