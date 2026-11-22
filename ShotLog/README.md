# ShotLog – Diari d'entrenaments de tir olímpic

**Activitat 06 – Menú i llista** · DAM 488, 489 i 1708 · Institut TIC de Barcelona

ShotLog és una aplicació Android (Kotlin + layouts XML) per portar el diari d'entrenaments de tir olímpic de 10 metres, en pistola i carabina d'aire. El tirador registra cada tret tocant una diana dibuixada a escala real, l'aplicació calcula la puntuació igual que un blanc electrònic i guarda l'entrenament amb les seves sèries i notes.

En aquesta activitat he fet:

1. Una **pantalla inicial (splash)** amb el logotip i una animació.
2. El **menú de navegació** del prototip: menú lateral (☰) i menú d'ajustos (⚙) a la barra superior.
3. **Llistes dinàmiques** amb `RecyclerView`, les seves classes **Adapter** i **Holder**, i els esdeveniments **afegir, modificar i eliminar** amb `Toast` i `Log`.
4. **Elements de prova** perquè la llista no surti buida.

---

## Índex

- [Com executar-la](#com-executar-la)
- [Navegació de l'aplicació](#navegació-de-laplicació)
- [1. Pantalla inicial (splash) amb animació](#1-pantalla-inicial-splash-amb-animació)
- [2. Menús de navegació](#2-menús-de-navegació)
- [3. Llistes dinàmiques: Adapter i Holder](#3-llistes-dinàmiques-adapter-i-holder)
- [4. Esdeveniments: afegir, modificar i eliminar](#4-esdeveniments-afegir-modificar-i-eliminar)
- [5. Registre de trets (pantalla estil SIUS)](#5-registre-de-trets-pantalla-estil-sius)
- [6. Preferències i tema fosc](#6-preferències-i-tema-fosc)
- [7. Accessibilitat i usabilitat](#7-accessibilitat-i-usabilitat)
- [8. Llenguatge Kotlin utilitzat](#8-llenguatge-kotlin-utilitzat)
- [Estructura del codi](#estructura-del-codi)
- [Pendent per a properes activitats](#pendent-per-a-properes-activitats)

---

## Com executar-la

1. Obrir la carpeta `ShotLog` amb Android Studio.
2. **Sync Project with Gradle Files** (descarrega les llibreries).
3. Executar a un emulador o mòbil amb **Android 7.0 (API 24)** o superior.

Llibreries afegides al projecte (a `gradle/libs.versions.toml`):

| Llibreria | Per a què la faig servir |
|---|---|
| `recyclerview` | Llistes dinàmiques |
| `viewpager2` | Pàgines lliscants de la guia d'inici |
| `drawerlayout` | Menú lateral |
| `core-splashscreen` | Evitar un doble splash a Android 12+ |
| `material` | Components Material 3 (toolbar, menú lateral, botons, diàlegs, xips…) |

---

## Navegació de l'aplicació

```
Splash ──► Inicio de sesión ──► Inicio (MainActivity)
              │                     │
              └─► Registro          ├─ ☰ Menú lateral: Inicio · Nuevo entrenamiento · Nueva competición
                                    │                  Mis entrenos · Amigos · Guía de inicio
                                    ├─ ⚙ Ajustos: Cuenta · Preferencias · Idioma · Cerrar sesión
                                    └─ Botons: Nuevo entrenamiento · Nueva competición · Mis entrenos · Guía

Nuevo entreno ──► Registro de tiros ──► Guardar entreno ──► Mis entrenos (llista)
Mis entrenos ──► Detalle del entreno ──► Editar  /  Eliminar (amb confirmació)
```

> La interfície de l'aplicació està en castellà; el codi i aquesta documentació expliquen cada part.

---

## 1. Pantalla inicial (splash) amb animació

**Fitxers:** `SplashActivity.kt`, `res/layout/activity_splash.xml`, `res/drawable/splash_*.xml`

Quan s'obre l'aplicació es veu una animació pensada per al tir olímpic:

1. La diana (el logotip) apareix poc a poc.
2. Un "tret" arriba des de davant (es fa petit) fins al centre de la diana.
3. En impactar surt una ona i la diana fa una petita sacsejada.
4. Apareix el nom **ShotLog**.
5. Al cap de 2,4 segons, **el logotip puja i s'encongeix fins a la seva posició a la pantalla d'inici de sessió**, mentre el nom s'esvaeix.

Com està fet:

- Les animacions són de `ViewPropertyAnimator` (`animate().alpha().scaleX()…`) amb `setStartDelay` per encadenar-les.
- El pas al login és una **transició d'element compartit**: el logotip del splash i el del login tenen el mateix `transitionName` (`logo_shotlog`), i Android calcula sol el moviment i el canvi de mida.

```kotlin
val opciones = ActivityOptionsCompat.makeSceneTransitionAnimation(
    this, diana, getString(R.string.transicion_logo)
)
startActivity(Intent(this, LoginActivity::class.java), opciones.toBundle())
```

- Faig servir la llibreria oficial `core-splashscreen`. A Android 12 o superior el sistema mostra la seva pròpia pantalla d'arrencada; així només en mostra el fons i de seguida comença la meva animació, sense dos splash seguits.
- Si l'usuari té les **animacions desactivades** al mòbil, no ha d'esperar: el temps es multiplica per l'escala d'animació del sistema.

---

## 2. Menús de navegació

**Fitxers:** `MainActivity.kt`, `res/layout/activity_main.xml`, `res/menu/menu_lateral.xml`, `res/menu/menu_ajustes.xml`, `res/layout/nav_cabecera.xml`

La pantalla d'Inici té dos menús, igual que al prototip:

### Menú lateral (☰)

- El layout és un `DrawerLayout` amb dos fills: el contingut de la pantalla i un `NavigationView` (el menú que surt per l'esquerra).
- Les opcions estan a `menu_lateral.xml`, cadascuna amb la seva icona, i la capçalera a `nav_cabecera.xml` (logotip i nom).
- El botó ☰ és la icona de navegació de la `MaterialToolbar`: en prémer-lo s'obre el menú.

```kotlin
toolbar.setNavigationOnClickListener { drawer.openDrawer(GravityCompat.START) }

menuLateral.setNavigationItemSelectedListener { opcion ->
    when (opcion.itemId) {
        R.id.navNuevoEntrenamiento -> nuevaSesion(competicion = false)
        R.id.navNuevaCompeticion -> nuevaSesion(competicion = true)
        R.id.navMisEntrenos -> abrir(SesionesActivity::class.java)
        R.id.navGuia -> abrir(GuiaActivity::class.java)
        // ...
    }
    drawer.closeDrawer(GravityCompat.START)
    opcion.itemId == R.id.navInicio
}
```

- Si el menú està obert, el botó **enrere** el tanca en lloc de sortir de l'aplicació (`OnBackPressedCallback`).

### Menú d'ajustos (⚙)

- És el menú d'opcions de la toolbar (`app:menu="@menu/menu_ajustes"`). La roda dentada té un **submenú** amb *Cuenta*, *Preferencias*, *Idioma* i *Cerrar sesión*.
- *Idioma* i *Cerrar sesión* obren **quadres de diàleg** (`MaterialAlertDialogBuilder`).

### Toolbar personalitzada

Totes les pantalles secundàries fan servir una `MaterialToolbar` amb un estil propi (`Widget.ShotLog.Toolbar` a `styles.xml`): fons de l'aplicació, fletxa enrere i títol amb la tipografia i el color del tema.

---

## 3. Llistes dinàmiques: Adapter i Holder

L'aplicació té **tres llistes dinàmiques personalitzades**:

| Llista | On es veu | Adapter | Holder |
|---|---|---|---|
| Entrenaments | *Mis entrenos* | `SesionesAdapter` (`ListAdapter`) | `SesionViewHolder` |
| Sèries d'un entrenament (mini dianes) | *Detalle* i *Guardar entreno* | `SeriesAdapter` | `SerieViewHolder` |
| Pàgines de la guia d'inici | *Guía de inicio* (`ViewPager2`) | `GuiaAdapter` | `PaginaViewHolder` |

### Què fa cada classe

- **Holder (`ViewHolder`)**: guarda les vistes d'una fila (textos, imatge…) perquè no s'hagin de buscar amb `findViewById` cada vegada que la fila es reutilitza. El `RecyclerView` només crea els holders que caben a la pantalla i els **recicla** quan fas scroll.
- **Adapter**: fa de pont entre les dades i la llista. Té tres funcions:
  - `onCreateViewHolder`: infla el layout d'una fila (`item_sesion.xml`) i crea el holder.
  - `onBindViewHolder`: posa les dades d'un element a un holder.
  - `getItemCount`: diu quants elements hi ha (al `ListAdapter` ja ve fet).

### `SesionesAdapter` (llista d'entrenaments)

**Fitxer:** `ui/SesionesAdapter.kt` · **Layout de la fila:** `res/layout/item_sesion.xml`

He fet servir un **`ListAdapter`** amb **`DiffUtil`**. Quan li passo una llista nova amb `submitList()`, compara la llista antiga amb la nova i **només anima el que ha canviat**: un entrenament nou entra, un de modificat s'actualitza i un d'eliminat desapareix.

```kotlin
class SesionesAdapter(
    private val alPulsar: (Entreno) -> Unit
) : ListAdapter<Entreno, SesionesAdapter.SesionViewHolder>(ComparadorEntrenos) {

    class SesionViewHolder(vista: View) : RecyclerView.ViewHolder(vista) {
        fun mostrar(entreno: Entreno, alPulsar: (Entreno) -> Unit) {
            SesionesAdapter.rellenarTarjeta(itemView, entreno)
            itemView.setOnClickListener { alPulsar(entreno) }   // element seleccionable
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SesionViewHolder {
        val vista = LayoutInflater.from(parent.context).inflate(R.layout.item_sesion, parent, false)
        return SesionViewHolder(vista)
    }

    override fun onBindViewHolder(holder: SesionViewHolder, position: Int) {
        holder.mostrar(getItem(position), alPulsar)
    }
}

/** DiffUtil: dos entrenaments són el mateix si tenen el mateix id; han canviat si les dades són diferents. */
private object ComparadorEntrenos : DiffUtil.ItemCallback<Entreno>() {
    override fun areItemsTheSame(viejo: Entreno, nuevo: Entreno) = viejo.id == nuevo.id
    override fun areContentsTheSame(viejo: Entreno, nuevo: Entreno) = viejo == nuevo
}
```

- Cada fila és una targeta (`MaterialCardView` + `ConstraintLayout`) amb el nom, la data, la modalitat, l'etiqueta *Entrenamiento/Competición* i la puntuació.
- **En tocar una fila** s'obre el detall de l'entrenament. L'acció es passa a l'adapter com una funció (`alPulsar`), així l'adapter no depèn de cap pantalla.
- La funció `rellenarTarjeta` també la fa servir la pantalla d'Inici per mostrar "Tu última sesión" amb el mateix disseny.
- A `SesionesActivity`, la llista es torna a carregar a `onResume()`. Així, en tornar d'afegir, editar o eliminar, es veu el canvi amb animació:

```kotlin
override fun onResume() {
    super.onResume()
    adapter.submitList(RepositorioEntrenos.todos())
}
```

### `SeriesAdapter` (sèries amb mini dianes)

**Fitxer:** `ui/SeriesAdapter.kt` · **Layout:** `res/layout/item_serie.xml`

És un `RecyclerView` **horitzontal** (`LinearLayoutManager` amb `orientation="horizontal"`). Cada element és una sèrie de 10 trets: una diana petita amb els impactes dibuixats, el número de sèrie i els punts. La mini diana fa servir la mateixa vista personalitzada que la pantalla de registre (`DianaView`), però sense interacció i amb el zoom ajustat perquè hi càpiguen tots els trets.

### Elements de prova

**Fitxer:** `DatosEjemplo.kt`

Perquè la llista no surti buida, l'aplicació comença amb **6 entrenaments d'exemple** (pistola i carabina, entrenament i competició). Els trets es generen al voltant del centre amb una dispersió realista (`Random(llavor).nextGaussian()`), i sempre surten els mateixos perquè la llavor és fixa.

---

## 4. Esdeveniments: afegir, modificar i eliminar

Les dades passen per `datos/RepositorioEntrenos.kt`, un `object` que guarda la llista d'entrenaments en memòria. Cada acció mostra un **`Toast`** a la pantalla i escriu un **`Log`** (etiqueta `ShotLog`, visible al Logcat):

| Esdeveniment | On passa | Toast | Log |
|---|---|---|---|
| **Afegir** | *Guardar entreno* → botó *Guardar* (o botó **+ Nuevo entreno** de la llista) | "Entreno añadido" | `Entreno añadido: id - nom (n disparos)` |
| **Modificar** | *Detalle* → *Editar* → *Guardar cambios* | "Entreno modificado" | `Entreno modificado: id - nom` |
| **Eliminar** | *Detalle* → *Eliminar sesión* → confirmació | "Entreno eliminado" | `Entreno eliminado: id` |

```kotlin
// Afegir (GuardarEntrenoActivity)
RepositorioEntrenos.anadir { id -> registro.aEntreno(id, nombre, notas, ahora) }
Toast.makeText(this, R.string.entreno_anadido, Toast.LENGTH_SHORT).show()

// Eliminar (DetalleSesionActivity), sempre amb un diàleg de confirmació abans
MaterialAlertDialogBuilder(this)
    .setTitle(R.string.eliminar_titulo)
    .setMessage(R.string.eliminar_mensaje)
    .setNegativeButton(R.string.cancelar, null)
    .setPositiveButton(R.string.eliminar) { _, _ ->
        RepositorioEntrenos.eliminar(idEntreno)
        Toast.makeText(this, R.string.entreno_eliminado, Toast.LENGTH_SHORT).show()
        finish()
    }
    .show()
```

A més, cada tret registrat es mostra al Logcat amb la seva puntuació i posició.

---

## 5. Registre de trets (pantalla estil SIUS)

**Fitxers:** `RegistroTirosActivity.kt`, `ui/DianaView.kt`, carpeta `tiro/`

Aquesta és la pantalla que crea un entrenament nou, és a dir, l'acció d'**afegir**. Està inspirada en els blancs electrònics SIUS que es fan servir a les galeries de tir.

- **`DianaView`** és una **vista personalitzada** que dibuixa la diana amb `Canvas` amb les **mesures oficials ISSF**:
  - **Pistola:** el 10 fa 11,5 mm i la zona negra va del 7 al 10.
  - **Carabina:** el 10 és un punt de 0,5 mm i la zona negra va del 4 al 9.
- **Zoom de 3 nivells**: tocant la diana s'amplia (1 → 2 → 3 → 1) amb una animació suau (`ValueAnimator`).
- **Botó REGISTRAR TIRO**: activa el mode registre. El botó canvia de color i de text, la diana es ressalta amb una vora verda i surt un avís que desapareix sol al cap de 3 segons. Mentre està actiu, tocar la diana marca un impacte.
- **Coordenades en mil·límetres**: cada tret es guarda en mm respecte al centre, no en píxels, perquè quedi al lloc correcte amb qualsevol zoom i qualsevol pantalla.
- **Puntuació**: `CalculadoraPuntuacion` la calcula com a les dianes reals, comptant el tret per l'anell més alt que toca la vora del balí (4,5 mm). Cada tret es mostra amb dècimes (10,4). A carabina els totals sumen les dècimes; a pistola, només la part entera.
- **Sèries de 10 trets**: l'onzè tret neteja la diana i comença una sèrie nova, però les anteriors es conserven. En mode competició es veuen els punts de cada sèrie i l'entrenament s'acaba sol als 60 o 40 trets.
- **Desfer** (fletxa ↶) per corregir l'últim tret.

La lògica (puntuació, sèries, totals) està separada de la pantalla a la carpeta `tiro/`, en classes Kotlin que no depenen d'Android i que es podran provar amb tests a l'activitat 11.

---

## 6. Preferències i tema fosc

**Fitxers:** `datos/PreferenciasApp.kt`, `PreferenciasActivity.kt`, `ShotLogApp.kt`, `res/values-night/`

- A *Preferencias* es pot triar el **tema clar o fosc** i la **modalitat per defecte** (Pistola o Carabina). Es guarden amb **`SharedPreferences`**.
- El tema s'aplica a tota l'aplicació amb `AppCompatDelegate.setDefaultNightMode()`. `ShotLogApp` (la classe `Application`) l'aplica en obrir l'aplicació, abans que surti cap pantalla.
- Els colors del tema fosc estan a `values-night/colors.xml`, amb els mateixos noms que els del tema clar, i Android tria sol els que toquen. La pantalla de registre en mode fosc té l'aspecte dels blancs electrònics SIUS.

---

## 7. Accessibilitat i usabilitat

Mesures que he aplicat:

- **Textos** a `strings.xml`, cap text escrit directament als layouts. Això permet traduir l'aplicació.
- **Mida de lletra en `sp`**, perquè respecti la mida de text que l'usuari tingui configurada al mòbil.
- **Zones tàctils de 48 dp com a mínim**: botons, enllaços i opcions tenen `minHeight` de 48 dp o més. Els botons principals fan entre 60 i 68 dp, pensats per fer servir l'aplicació a la galeria de tir.
- **Contrast**: he revisat els colors amb la inspecció d'Android Studio. Vaig enfosquir el verd dels enllaços perquè passés de 4,3:1 a 5,4:1, per sobre del mínim de 4,5:1.
- **TalkBack (lector de pantalla)**:
  - Totes les icones i imatges amb significat tenen `contentDescription`, i les decoratives porten `importantForAccessibility="no"`.
  - La diana té una descripció que canvia sola, per exemple "Diana de pistola, zoom 2 de 3, 7 impactos".
  - L'últim tret i el total són `accessibilityLiveRegion="polite"`, així TalkBack llegeix la puntuació quan canvia.
  - La diana implementa `performClick()`, de manera que el zoom també es pot fer servir amb TalkBack.
- **Animacions**: el splash respecta l'opció del mòbil de desactivar les animacions.
- **No dependre només del color**: el mode registre canvia el color del botó, però també el seu text, i mostra un avís escrit.
- **Formularis**: `autofillHints` als camps de correu i contrasenya, `labelFor` a les etiquetes i errors en vermell amb missatge sota el camp (pantalla de registre).
- **Accions destructives** (eliminar, sortir sense guardar, tancar sessió) amb **diàleg de confirmació**.
- **Tema fosc** per a qui el prefereixi o per fer servir el mòbil amb poca llum.
- **Pantalla completa (edge-to-edge)**: el contingut respecta la barra d'estat, la de navegació i el teclat (`aplicarMargenesSistema()`).

---

## 8. Llenguatge Kotlin utilitzat

| Concepte | On el faig servir |
|---|---|
| `object` (singleton) | `RepositorioEntrenos`, `DatosEjemplo`, `CalculadoraPuntuacion`, `PreferenciasApp` |
| `data class` | `Disparo`, `Serie`, `Entreno`, `PaginaGuia` |
| `enum class` | `TipoDiana` (mesures de cada diana), `NivelZoom`, `Modalidad`, `ModoEntreno` |
| **Funcions d'extensió** | `Long.comoFecha()` (data en format dd/MM/yyyy), `Int.comoPuntos()` (dècimes a text "10,4"), `Entreno.textoTotal()`, `AppCompatActivity.aplicarMargenesSistema()`, `irAlLogin()` |
| Lambdes i funcions com a paràmetre | Clic dels adapters (`alPulsar`), callbacks de `DianaView` |
| `companion object` | Constants dels `Intent` (`EXTRA_ID`…) i `SesionesAdapter.rellenarTarjeta()` |

Exemple de funció d'extensió per a la data:

```kotlin
fun Long.comoFecha(): String =
    SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(this))

// ús: entreno.fecha.comoFecha()  →  "04/11/2026"
```

---

## Estructura del codi

```
app/src/main/java/com/example/shotlog/
├── SplashActivity.kt          Pantalla inicial animada
├── LoginActivity.kt           Inicio de sesión
├── RegistroActivity.kt        Registro (amb validació dels camps)
├── MainActivity.kt            Inici: menú lateral i menú d'ajustos
├── NuevaSesionActivity.kt     Triar modalitat, mode i format
├── RegistroTirosActivity.kt   Registre de trets (estil SIUS)
├── GuardarEntrenoActivity.kt  Nom i notes → AFEGIR
├── SesionesActivity.kt        Mis entrenos (llista)
├── DetalleSesionActivity.kt   Detall → ELIMINAR
├── EditarSesionActivity.kt    Editar → MODIFICAR
├── GuiaActivity.kt            Guia d'inici (ViewPager2)
├── PreferenciasActivity.kt    Tema i modalitat per defecte
├── CuentaActivity.kt          Compte
├── ShotLogApp.kt              Application: aplica el tema guardat
├── DatosEjemplo.kt            Entrenaments de prova
├── Utilidades.kt              Funcions d'extensió comunes
├── datos/                     RepositorioEntrenos, PreferenciasApp
├── modelo/                    Entreno, Serie, Disparo, formats
├── tiro/                      Lògica: diana ISSF, puntuació, sèries, zoom
└── ui/                        DianaView, SesionesAdapter, SeriesAdapter
```

---

## Pendent per a properes activitats

- **Persistència (activitat 09)**: ara els entrenaments es guarden en memòria i es perden en tancar l'aplicació. Només caldrà canviar `RepositorioEntrenos`.
- **Filtre de la llista (activitat 07.2)**: el cercador i els filtres de *Mis entrenos* ja tenen el disseny fet, però encara no filtren.
- **Inici de sessió real** amb Firebase, **Amics** i **xat**.
- **Fotografia de la diana** amb la càmera.
