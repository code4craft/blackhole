blackhole
=========

A simple DNS authoritative server.It can easily be configured to intercept some kind of request to one address.

###Design principles

*	**Keep it simple and stupid** 
	
	Blackhole only implements small part of DNS function. It is designed as an authoritative DNS server and can't lookup DNS records recursively from DNS masters.You can use it as a "mask" with other alternative DNS server such as google dns.
	
*	**Convention over configuration** 

	There are no complicated configs for DNS such as TTL and SOA, no xmls files for Java program, except pom.xml for maven :)
