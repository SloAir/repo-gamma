# repo-gamma
Prevajanje

1 OSNOVA
country "luxembourg" {
    city "luxembourg" {
        airplane "delta" {
            point ( 5 , 1 )
        }
        airport "luxembourg airport" {
            building "pickup and dropoff" {
                box ( point ( 3 , 5 ) , point ( 8 , 9 ) )
            }
            building "checkin" {
                box ( point ( 3 , 3 ) , point ( 4 , 4 ) )
            }
            terminal "terminal S" {
                gate "A1" {
                    circle ( point ( 1 , 1 ) , 0.5 )
                }
                gate "A2" {
                    circle ( point ( 1 , 1 ) , 0.5 )
                }
            }
            runway "1" {
                line ( point ( -10 , -12) , point ( -120 , -12 ) )
            }
        }
    }
}


2 KONSTRUKTI

    2.1 Števila
        1
        1.4
        -1
        -1.4

    2.2 Nizi
        "NIZ"

    2.3 Točke
        Z točkami predstavimo lokacije na zamljevidu, v tem primeru je X longituda in Y latituda.
        point (X, Y)

    2.4 Bloki

        2.4.1
            Blok country predstavlja državo, ki jo opisujemo. Vsebuje lahko bloke za opis letal in letališč. To je tudi glavni element v jeziku.
            country "NAME" {
                BLOCKS
            }
        2.4.2
            Blok city predtavlja mesto, ki jo opisujemo z bloki airport in building.
            city "NAME" {
                BLOCKS
            }

        2.4.3
            Blok airplane predstavlja letalo, ki ga opisujemo. Vsebuje ukaze za izris letala, ki izriče krog.
            airplane "NAME" {
                COMMANDS
            }

        2.4.4
            Blok airport predstavlja letališče, ki ga opisujemo. Vsebuje čahko bloke za opis building, terminal in runway.
            airport "NAME" {
                BLOCKS
            }
        2.4.5
            Blok building predstavlja zgradbe znotraj letališča, kot so trgovina, pickup, dropoff, checkin. Vsebuje ukaze za izris. Izris je lahko z box ali circle.
            building "NAME" {
                COMMANDS
            }
        2.4.6
            Blok runway predstavlja izletno pot. Vsebuje ukaze za izris z line.
            runway "NAME" {
                COMMANDS
            }

        2.4.7
            Blok terminal predstavlja terminal. Vsebuje bloke za opis gate.
            terminal "NAME" {
                BLOCKS
            }

        2.4.8
            Blok gate predtavlja vrata za vkrcanje. Vsebuje ukaze za izris vrat.
            gate "NAME" {
                COMMANDS
            }


    2.5 Ukazi

        2.5.1
            Ukaz line izriše črto, med podanima lokacijama.
            line (POINT, POINT)

        2.5.2
            Ukaz box izriše pravokotnik, med podanima lokacijama. Torej prva lokacija je
            zgornji levi kot pravokotnika druga lokacija pa je spodnji desni kot.
            box (POINT, POINT)

        2.5.3
            Ukaz circle izriše krog z izbranim polmerom na podani lokaciji. Ta lokacija predstavlja center kroga.
            circle (POINT, RADIUS)
