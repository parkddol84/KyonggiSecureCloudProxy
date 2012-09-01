gcc kgu_kscp_erasure_Encoder.c cauchy.c galois.c liberation.c reed_sol.c jerasure.c -o libEncoder.so -shared -I/usr/local/java/include/ -I/usr/local/java/include/linux/

gcc kgu_kscp_erasure_Decoder.c cauchy.c galois.c liberation.c reed_sol.c jerasure.c -o libDecoder.so -shared -I/usr/local/java/include/ -I/usr/local/java/include/linux/
