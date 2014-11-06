(ns mvxcvi.crypto.pgp.util
  "Utility functions for dealing with byte content."
  (:require
    [byte-streams :as bytes]
    [clojure.string :as str])
  (:import
    (java.io
      InputStream)
    (org.bouncycastle.openpgp
      PGPObjectFactory)))

; TODO: move to 'io'?

;; ## Byte Processing

(def ^:dynamic *buffer-size*
  "Size of buffer to use in data functions."
  1024)


(defn apply-bytes
  "Calls the given function on chunks of the byte sequence read from the given
  data source. The function should accept a byte array and a number of bytes to
  use from it."
  [source f]
  (with-open [stream (bytes/to-input-stream source)]
    (let [buffer (byte-array *buffer-size*)]
      (loop [n (.read stream buffer)]
        (when (pos? n)
          (f buffer n)
          (recur (.read stream buffer)))))))


(defn hex-str
  "Formats an 8-byte long value as a hexadecimal string."
  [^long value]
  (format "%016x" value))



;; ## PGP Utilities

(defn read-pgp-objects
  "Decodes a lazy sequence of PGP objects from an input stream."
  [^InputStream input]
  (let [factory (PGPObjectFactory. input)]
    (take-while identity (repeatedly #(.nextObject factory)))))
