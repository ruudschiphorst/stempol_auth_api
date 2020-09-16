# stempol_auth_api

Dit is een simpel Spring Boot project die een REST webserver de lucht in brengt en een aantal endpoints beschikbaar maakt. Een gebruiker kan zijn/haar credentials versturen naar het /api/auth/generatetoken endpoint. Deze controleert de credentials in een _interne_ LDAP server en genereert bij een succesvolle check een JWT die aan de gebruiker wordt geserveerd. Deze JWT bevat de volgende gegevens:
* Username
* Issued at
* Expiration (15 minuten)
* Welke groepen de gebruiker allemaal in zit


