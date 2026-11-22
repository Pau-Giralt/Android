# Module ShotLog

Diari d'entrenaments de tir olímpic de 10 metres (pistola i carabina d'aire).

El tirador registra cada tret tocant una diana dibuixada a escala real (mesures ISSF), l'aplicació
calcula la puntuació com un blanc electrònic i guarda l'entrenament amb les seves sèries i notes.

Activitat 06 (Menú i llista): pantalla inicial animada, menú lateral i menú d'ajustos,
llistes dinàmiques amb RecyclerView (Adapter i Holder) i esdeveniments afegir, modificar i eliminar.

# Package com.example.shotlog

Pantalles de l'aplicació (Activities): splash, inici de sessió i registre, inici amb els menús,
nou entrenament, registre de trets, llista d'entrenaments, detall, edició, guia, preferències i compte.
També hi ha les funcions d'extensió comunes (Utilidades.kt) i les dades de prova (DatosEjemplo).

# Package com.example.shotlog.tiro

Lògica del tir, separada de la interfície: mesures oficials de les dianes (TipoDiana),
nivells de zoom (NivelZoom), càlcul de la puntuació (CalculadoraPuntuacion) i
comptatge de trets, sèries de 10 i totals (RegistroEntreno).

# Package com.example.shotlog.modelo

Model de dades: un Entreno té Series, i cada Serie té Disparos. Les posicions dels trets es guarden
en mil·límetres respecte al centre de la diana. Inclou les funcions d'extensió per mostrar
puntuacions i dates.

# Package com.example.shotlog.datos

Accés a les dades: el repositori d'entrenaments (en memòria fins a l'activitat 09) i les
preferències de l'aplicació guardades amb SharedPreferences.

# Package com.example.shotlog.ui

Components de la interfície: la vista personalitzada de la diana (DianaView) i els Adapters
dels RecyclerView (SesionesAdapter i SeriesAdapter) amb els seus Holders.
