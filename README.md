# Wakeify 0.1 — toesteltest

<img src="assets/wakeify-logo.svg" alt="Wakeify" width="96" />

Installeer Wakeify-0.1.apk op je telefoon. Sta installatie vanuit de gebruikte browser of bestandsapp toe als Android daarom vraagt.

1. Open Wakeify en geef cameratoegang via de testknop.
2. Test de vijf standen. Elke stand duurt drie seconden; na vijftien seconden schakelt de zaklamp uit. Kijk naar het licht op een muur, niet rechtstreeks in de LED.
3. Bevestig alleen als je daadwerkelijk vijf verschillende helderheden zag.
4. Kies een tijd en stel het lichtalarm in. Sta exacte alarmen toe als Android dat vraagt en druk daarna opnieuw op instellen.

De tijd is het BEGIN van de lichtreeks: stand 1 op minuut 0, stand 2 op minuut 2, stand 3 op minuut 4, stand 4 op minuut 6, stand 5 op minuut 8. Op minuut 10 gaat het licht uit. Dit is een eenmalig lichtalarm zonder geluid. Een herstart wist het Android-alarm; stel het daarna opnieuw in. Stoppen kan in de app of via de melding tijdens de reeks.

## Galaxy S10-beperking

Android 13 introduceert de openbare API voor zaklampsterkte. De gewone S10 heeft officieel Android 12. Deze testversie probeert op oudere Android-versies de niet-openbare Samsung-methode setTorchMode(String, boolean, int). Het bestaan, de toegankelijkheid en de betekenis van de niveaus zijn NIET bevestigd op jouw firmware. Android kan de methode blokkeren. Bij ontbreken of mislukken meldt de app de beperking en wordt geen ondersteunde vijfstandenwerking geclaimd. Er is geen root of wijziging van systeeminstellingen ingebouwd.

De APK is lokaal gebouwd en ondertekend met een testsleutel; geen Play Store-release. Alarm bij vergrendeld scherm, Samsung-batterijbeheer, warmtebegrenzing en echte helderheden moeten nog op de S10 getest worden. Gebruik tot die verificatie je normale wekker naast deze testapp. Schakel voor een proef Samsung diepe slaap voor deze app uit. Camera-gebruik door een andere app kan de zaklamp onderbreken.

## Bouwen

Voer build.ps1 uit vanuit deze map. Het gebruikt de bestaande lokale SDK/JDK uit ../android-build-tools/paths.json en heeft geen externe bibliotheken nodig.
