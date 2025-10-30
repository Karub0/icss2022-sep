# Bijlage – Eisenverantwoording ICSS Compiler

## Algemene eisen

| ID   | Omschrijving                                                                                  | Prio | Status  |
|------|-----------------------------------------------------------------------------------------------|------|---------|
| AL01 | De code behoudt de packagestructuur van de aangeleverde startcode. Toegevoegde code bevindt zich in de relevante packages. | Must | Voldoet |
| AL02 | Alle code compileert en is te bouwen met Maven 3.6 of hoger, onder OpenJDK 13.               | Must | Voldoet |
| AL03 | De code is goed geformatteerd, voorzien van commentaar, correcte variabelenamen gebruikt, bevat geen onnodig ingewikkelde constructies en is onderhoudbaar. | Must | Voldoet |
| AL04 | De compiler is eigen werk en voldoet aan APP-6.                                               | Must | Voldoet |

## Parser

| ID   | Omschrijving                                                                                  | Prio | Status  |
|------|-----------------------------------------------------------------------------------------------|------|---------|
| PA00 | De parser maakt zinvol gebruik van een eigen implementatie van een stack generic voor ASTNode | Must | Voldoet |
| PA01 | Implementeer een grammatica plus listener die AST’s kan maken voor ICSS documenten die eenvoudige opmaak kan parseren | Must | Voldoet |
| PA02 | Breid grammatica en listener uit voor variabele assignments en gebruik ervan                 | Must | Voldoet |
| PA03 | Breid grammatica en listener uit voor optellen, aftrekken en vermenigvuldigen                | Must | Voldoet |
| PA04 | Breid grammatica en listener uit voor if/else-statements                                      | Must | Voldoet |
| PA05 | PA01 t/m PA04 leveren minimaal 30 punten op                                                   | Must | Voldoet |

## Checker

| ID   | Omschrijving                                                                                  | Prio | Status  |
|------|-----------------------------------------------------------------------------------------------|------|---------|
| CH00 | Minimaal vier van onderstaande checks moeten zijn geïmplementeerd                             | Must | Voldoet |
| CH01 | Controleer of er geen variabelen worden gebruikt die niet gedefinieerd zijn                   | Should | Voldoet |
| CH02 | Controleer operandtypen bij optellen, aftrekken en vermenigvuldigen                           | Should | Voldoet |
| CH03 | Controleer dat kleuren niet worden gebruikt in operaties                                      | Should | Voldoet |
| CH04 | Controleer dat type van value bij declaraties klopt met property                               | Should | Voldoet |
| CH05 | Controleer dat de conditie bij een if-statement boolean is                                    | Should | Voldoet |
| CH06 | Controleer dat variabelen enkel binnen hun scope gebruikt worden                               | Must | Voldoet |

## Transformaties

| ID   | Omschrijving                                                                                  | Prio | Status  |
|------|-----------------------------------------------------------------------------------------------|------|---------|
| TR01 | Evalueer expressies: vervang Expression knopen door Literal knopen met berekende waarden      | Must | Voldoet |
| TR02 | Evalueer if/else expressies: verwijder IfClauses en behoud juiste body                         | Must | Voldoet |

## Generator

| ID   | Omschrijving                                                                                  | Prio | Status  |
|------|-----------------------------------------------------------------------------------------------|------|---------|
| GE01 | Implementeer de generator die de AST naar een CSS2-compliant string omzet                      | Must | Voldoet |
| GE02 | Zorg dat de CSS met twee spaties inspringing per scopeniveau gegenereerd wordt                 | Must | Voldoet |

> Er zijn geen extra taaluitbereidingen gemaakt.