"""
Cambia RoQui entre PostgreSQL y MariaDB.

    python env.py ps
    python env.py check mariadb
    python env.py mariadb
    python env.py postgres
"""

import glob
import os
import re
import shutil
import socket
import subprocess
import sys
import urllib.error
import urllib.request

# ==========================================================================
#  CONFIGURACION
#  aqui se cambia si se mueve una ruta, una clave o un puerto
# ==========================================================================

# este archivo vive dentro de la carpeta RoQui
RAIZ = os.path.dirname(os.path.abspath(__file__))
PROPERTIES = os.path.join(RAIZ, "src", "main", "resources", "application.properties")
GRADLE = os.path.join(RAIZ, "build.gradle.kts")
MODELOS_DIR = os.path.join(RAIZ, "src", "main", "kotlin", "dev", "joguenco", "roqui")

# datos de cada motor
MOTORES = {
    "postgres": {
        "titulo": "PostgreSQL",
        "host": "localhost",
        "puerto": 5432,
        "base": "roqui",
        "usuario": "roqui",
        "clave": "r",
        "url": "jdbc:postgresql://localhost:5432/roqui",
        "driver": "org.postgresql.Driver",
        "dialecto": "org.hibernate.dialect.PostgreSQLDialect",
        "gradle": 'runtimeOnly("org.postgresql:postgresql")',
        # el cliente de consola, se busca primero en el PATH
        "ejecutables": ["psql"],
        "donde_buscar": [
            r"C:\Program Files\PostgreSQL\*\bin\psql.exe",
            "/usr/bin/psql",
            "/usr/local/bin/psql",
            "/opt/homebrew/bin/psql",
        ],
    },
    "mariadb": {
        "titulo": "MariaDB",
        "host": "localhost",
        "puerto": 3306,
        "base": "donpos",
        "usuario": "donpos",
        "clave": "d",
        "url": "jdbc:mariadb://localhost:3306/donpos",
        "driver": "org.mariadb.jdbc.Driver",
        "dialecto": "org.hibernate.dialect.MariaDBDialect",
        "gradle": 'runtimeOnly("org.mariadb.jdbc:mariadb-java-client")',
        # en MariaDB nuevo el comando se llama mariadb, en el viejo mysql
        "ejecutables": ["mariadb", "mysql"],
        "donde_buscar": [
            r"C:\Program Files\MariaDB*\bin\mariadb.exe",
            r"C:\Program Files\MariaDB*\bin\mysql.exe",
            "/usr/bin/mariadb",
            "/usr/bin/mysql",
            "/usr/local/bin/mysql",
            "/opt/homebrew/bin/mysql",
        ],
    },
}

# que tipo lleva el @Id de cada modelo en cada motor
# los que no estan aqui no se tocan, su tipo es igual en las dos bases
MODELOS = {
    "invoice/model/Invoice.kt": ("Long", "UUID"),
    "invoice/model/InvoiceDetail.kt": ("Long", "UUID"),
    "invoice/model/Payment.kt": ("Long", "UUID"),
    "invoice/model/ReportInvoice.kt": ("Long", "UUID"),
    "invoice/model/TaxDetail.kt": ("Long", "UUID"),
    "liquidation/model/Liquidation.kt": ("Long", "UUID"),
    "liquidation/model/LiquidationDetail.kt": ("Long", "UUID"),
    "liquidation/model/LiquidationTax.kt": ("Long", "UUID"),
    "liquidation/model/ReportLiquidation.kt": ("Long", "UUID"),
    "note/credit/model/CreditNote.kt": ("Long", "UUID"),
    "note/credit/model/CreditNoteDetail.kt": ("Long", "UUID"),
    "note/credit/model/ReportCreditNote.kt": ("Long", "UUID"),
    "note/debit/model/DebitNote.kt": ("Long", "UUID"),
    "note/debit/model/DebitNoteDetail.kt": ("Long", "UUID"),
    "note/debit/model/ReportDebitNote.kt": ("Long", "UUID"),
    "note/delivery/model/DeliveryNote.kt": ("Long", "UUID"),
    "note/delivery/model/DeliveryNoteDetail.kt": ("Long", "UUID"),
    "note/delivery/model/DeliveryNoteReceiver.kt": ("Long", "UUID"),
    "note/delivery/model/ReportDeliveryNote.kt": ("Long", "UUID"),
    "security/model/User.kt": ("Int", "Long"),
    "withhold/model/ReportWithhold.kt": ("Long", "UUID"),
    "withhold/model/Withhold.kt": ("Long", "UUID"),
    "withhold/model/WithholdDetail.kt": ("Long", "UUID"),
    "withhold/model/WithholdDocumentTax.kt": ("Long", "UUID"),
    "withhold/model/WithholdSupport.kt": ("Long", "UUID"),
    # este no es un modelo, es el dto de los reportes
    # lleva el mismo tipo porque recibe el id de los modelos de arriba
    "common/dto/ReportReciptDto.kt": ("Long", "UUID"),
}

# los servicios que se revisan en el ps
# (nombre, puerto, ruta para preguntarle si contesta)
SERVICIOS = [
    ("RoQui (back)", 8080, "/ping"),
    ("roqui-client", 5173, "/"),
]

# columnas que se llaman distinto en cada base
# en postgres 'authorization' es palabra clave, por eso alla se llama authorization_code
COLUMNAS = {
    "electronic/model/Document.kt": [
        # (nombre en postgres, nombre en mariadb)
        ("authorization_code", "authorization"),
    ],
}

# las 5 lineas de conexion que hay que cambiar en application.properties
CLAVES = [
    ("spring.datasource.url", "url"),
    ("spring.datasource.username", "usuario"),
    ("spring.datasource.password", "clave"),
    ("spring.datasource.driver-class-name", "driver"),
    ("spring.jpa.database-platform", "dialecto"),
]

# la linea del id viene de dos formas:
#     @Id val id: Long? = null      en los modelos
#         val id: Long? = null,     en el dto de reportes, con coma al final
LINEA_ID = re.compile(r"^(\s*)(@Id )?val id: (\w+)\? = null(,?)\s*$")
LINEA_ID_COMENTADA = re.compile(r"^\s*//\s*(@Id )?val id: \w+\? = null,?\s*$")


# ==========================================================================
#  LEER Y GUARDAR ARCHIVOS
#  cosas basicas, las usan todos los comandos
# ==========================================================================


def leer(ruta):
    with open(ruta, encoding="utf-8") as f:
        return f.read().splitlines()


def guardar(ruta, lineas):
    with open(ruta, "w", encoding="utf-8", newline="\n") as f:
        f.write("\n".join(lineas) + "\n")


def relleno(texto, ancho=42):
    # rellena con puntos para que todo quede alineado
    return "  " + texto + " " + "." * (ancho - len(texto)) + " "


def es_linea_activa(linea, clave):
    # una linea activa es la que no empieza con #
    return linea.strip().startswith(clave + "=")


def valor_activo(lineas, clave):
    # devuelve el valor de una propiedad, ignorando las comentadas
    for linea in lineas:
        if es_linea_activa(linea, clave):
            return linea.split("=", 1)[1].strip()
    return None


# ==========================================================================
#  REVISAR COMO ESTAN LAS COSAS
#  usado por: ps, check, mariadb, postgres
# ==========================================================================


def motor_activo():
    # busca en que motor estamos, mirando la url de application.properties
    url = valor_activo(leer(PROPERTIES), "spring.datasource.url")
    if url is None:
        return None
    if "postgresql" in url:
        return "postgres"
    if "mariadb" in url:
        return "mariadb"
    return None


def tipo_actual(ruta_completa):
    # saca el tipo del @Id que tiene ahora el modelo
    for linea in leer(ruta_completa):
        m = LINEA_ID.match(linea)
        if m:
            return m.group(3)
    return None


def necesita_limpieza(ruta_completa, esperado):
    # el archivo quedo con la linea vieja comentada, o con el import de UUID
    # que no le toca. las dos cosas hay que arreglarlas
    lineas = leer(ruta_completa)

    for linea in lineas:
        if LINEA_ID_COMENTADA.match(linea):
            return True

    tiene = "import java.util.UUID" in lineas
    comodin = "import java.util.*" in lineas
    if esperado == "UUID" and not tiene and not comodin:
        return True
    if esperado != "UUID" and tiene:
        return True

    return False


def revisar_modelos(motor):
    # compara cada modelo contra el tipo que le toca en ese motor
    # sucios son los que quedaron con la linea comentada de mas
    indice = 0 if motor == "postgres" else 1
    bien, mal, rotos, sucios = [], [], [], []

    for relativo, tipos in MODELOS.items():
        ruta = os.path.join(MODELOS_DIR, relativo.replace("/", os.sep))
        esperado = tipos[indice]

        if not os.path.exists(ruta):
            rotos.append((relativo, "no existe el archivo"))
            continue

        if necesita_limpieza(ruta, esperado):
            sucios.append(relativo)

        tiene = tipo_actual(ruta)
        if tiene is None:
            rotos.append((relativo, "no encontre la linea del @Id"))
        elif tiene == esperado:
            bien.append(relativo)
        else:
            mal.append((relativo, tiene, esperado))

    return bien, mal, rotos, sucios


# ==========================================================================
#  PROBAR LA CONEXION A LA BASE
#  usado por: ps, check, mariadb, postgres
# ==========================================================================


def buscar_cliente(motor):
    # busca el cliente de consola: primero en el PATH, si no en los sitios tipicos
    # asi funciona igual en windows y en linux
    cfg = MOTORES[motor]

    for nombre in cfg["ejecutables"]:
        ruta = shutil.which(nombre)
        if ruta:
            return ruta

    for patron in cfg["donde_buscar"]:
        encontrados = sorted(glob.glob(patron), reverse=True)
        if encontrados:
            return encontrados[0]

    return None


def puerto_abierto(host, puerto):
    s = socket.socket()
    s.settimeout(2)
    try:
        s.connect((host, puerto))
        return True
    except OSError:
        return False
    finally:
        s.close()


def esta_corriendo(puerto, ruta):
    # le preguntamos por http, si contesta algo es que esta vivo
    # no usamos socket porque en windows a veces da falso negativo
    url = "http://localhost:%s%s" % (puerto, ruta)
    try:
        urllib.request.urlopen(url, timeout=2)
        return "corriendo"
    except urllib.error.HTTPError:
        # contesto aunque sea con error, o sea que ahi hay algo
        return "corriendo"
    except Exception:
        return "APAGADO"


def consultar(motor, sql):
    # corre una consulta con el cliente de consola de cada motor
    # devuelve (salio_bien, texto)
    cfg = MOTORES[motor]
    cliente = buscar_cliente(motor)
    if cliente is None:
        return False, "no encuentro el cliente de %s (%s)" % (
            cfg["titulo"], " o ".join(cfg["ejecutables"]))

    entorno = os.environ.copy()
    if motor == "postgres":
        entorno["PGPASSWORD"] = cfg["clave"]
        cmd = [cliente, "-h", cfg["host"], "-U", cfg["usuario"],
               "-d", "postgres", "-t", "-A", "-c", sql]
    else:
        cmd = [cliente, "-h", cfg["host"], "-u", cfg["usuario"],
               "-p" + cfg["clave"], "-sN", "-e", sql]

    try:
        r = subprocess.run(cmd, capture_output=True, text=True, timeout=15, env=entorno)
    except subprocess.TimeoutExpired:
        return False, "la base no contesto en 15 segundos"

    if r.returncode != 0:
        return False, (r.stderr or r.stdout).strip().splitlines()[0]
    return True, r.stdout.strip()


def probar_conexion(motor):
    # los 3 pasos: puerto, usuario y que la base exista
    cfg = MOTORES[motor]

    print(relleno("[1/3] puerto %s" % cfg["puerto"]), end="")
    if not puerto_abierto(cfg["host"], cfg["puerto"]):
        print("CERRADO")
        print()
        print("  %s no responde en %s:%s" % (cfg["titulo"], cfg["host"], cfg["puerto"]))
        print("  revisa que el servicio este corriendo")
        return False
    print("OK")

    print(relleno("[2/3] usuario %s" % cfg["usuario"]), end="")
    if motor == "postgres":
        sql = "select datname from pg_database where not datistemplate;"
    else:
        sql = "SHOW DATABASES;"
    ok, salida = consultar(motor, sql)
    if not ok:
        print("ERROR")
        print()
        print("  " + salida)
        return False
    print("OK")

    print(relleno("[3/3] base '%s'" % cfg["base"]), end="")
    bases = [b.strip() for b in salida.splitlines() if b.strip()]
    if cfg["base"] not in bases:
        print("NO EXISTE")
        print()
        print("  bases que si hay: " + ", ".join(bases))
        if motor == "mariadb":
            print()
            print("  creala corriendo, en este orden:")
            print("    DonPos/src/main/resources/com/unicenta/pos/scripts/MariaDB-create.sql")
            print("    DonPos/src/main/resources/com/unicenta/pos/scripts/MariaDB-data-EC.sql")
        return False

    vistas = contar_vistas(motor)
    print("OK, %s vistas v_ele_*" % vistas)
    return True


def contar_vistas(motor):
    cfg = MOTORES[motor]
    if motor == "postgres":
        sql = ("select count(*) from pg_views where schemaname='public' "
               "and viewname like 'v_ele_%';")
    else:
        sql = ("SELECT count(*) FROM information_schema.VIEWS "
               "WHERE TABLE_SCHEMA='%s' AND TABLE_NAME LIKE 'v_ele_%%';" % cfg["base"])

    # para contar hay que entrar a la base, no a la de sistema
    cliente = buscar_cliente(motor)
    if cliente is None:
        return "?"

    entorno = os.environ.copy()
    if motor == "postgres":
        entorno["PGPASSWORD"] = cfg["clave"]
        cmd = [cliente, "-h", cfg["host"], "-U", cfg["usuario"],
               "-d", cfg["base"], "-t", "-A", "-c", sql]
    else:
        cmd = [cliente, "-h", cfg["host"], "-u", cfg["usuario"],
               "-p" + cfg["clave"], cfg["base"], "-sN", "-e", sql]

    try:
        r = subprocess.run(cmd, capture_output=True, text=True, timeout=15, env=entorno)
        return r.stdout.strip() or "?"
    except Exception:
        return "?"


# ==========================================================================
#  ESCRIBIR LOS CAMBIOS EN LOS ARCHIVOS
#  usado por: mariadb, postgres. son los unicos que modifican
# ==========================================================================


def es_linea_driver(linea):
    # la linea del driver activo en build.gradle.kts
    if not linea.startswith("runtimeOnly("):
        return False
    return any(x in linea for x in ("postgresql", "mariadb", "mssql-jdbc"))


def quitar_bloques_comentados(lineas):
    # deja un solo bloque de conexion, el activo
    # los de mariadb y postgres comentados sobran, env.py ya sabe sus datos
    # el de SQL Server no se toca, ese lo dejamos como esta
    salida = []
    saltando = False

    for linea in lineas:
        limpia = linea.strip()

        if limpia in ("# MariaDB connection properties",
                      "# PostgreSQL connection properties"):
            # miramos si el bloque de abajo esta comentado, si si lo saltamos
            saltando = "pendiente"
            titulo = linea
            continue

        if saltando == "pendiente":
            if limpia.startswith("#spring."):
                saltando = True      # era un bloque comentado, se va entero
                continue
            saltando = False
            salida.append(titulo)    # era el bloque activo, devolvemos su titulo

        if saltando is True:
            if limpia.startswith("#spring.") or limpia == "":
                continue
            saltando = False

        salida.append(linea)

    return salida


def cambiar_properties(motor, solo_revisar):
    # cambia las 5 lineas de conexion y el titulo del bloque
    cfg = MOTORES[motor]
    lineas = leer(PROPERTIES)
    cambios = []
    primera = None

    for i, linea in enumerate(lineas):
        for clave, dato in CLAVES:
            if es_linea_activa(linea, clave):
                nuevo = "%s=%s" % (clave, cfg[dato])
                if linea != nuevo:
                    cambios.append((clave, linea.split("=", 1)[1], cfg[dato]))
                    if not solo_revisar:
                        lineas[i] = nuevo
                if primera is None:
                    primera = i

    # el comentario de arriba del bloque tiene que decir el motor correcto
    if primera is not None and not solo_revisar:
        for i in range(primera - 1, -1, -1):
            if "connection properties" in lineas[i]:
                lineas[i] = "# %s connection properties" % cfg["titulo"]
                break

    if not solo_revisar:
        lineas = quitar_bloques_comentados(lineas)
        guardar(PROPERTIES, lineas)
    return cambios


def cambiar_gradle(motor, solo_revisar):
    # cambia la linea del driver, la que no esta comentada
    cfg = MOTORES[motor]
    lineas = leer(GRADLE)
    cambios = []

    for i, linea in enumerate(lineas):
        limpia = linea.strip()
        if es_linea_driver(limpia):
            if limpia != cfg["gradle"]:
                cambios.append((limpia, cfg["gradle"]))
                if not solo_revisar:
                    espacios = linea[: len(linea) - len(linea.lstrip())]
                    lineas[i] = espacios + cfg["gradle"]
            break

    if cambios and not solo_revisar:
        guardar(GRADLE, lineas)
    return cambios


def revisar_columnas(motor):
    # busca las columnas que se llaman distinto en cada base
    # devuelve la lista de las que hay que cambiar
    indice = 0 if motor == "postgres" else 1
    pendientes = []

    for relativo, pares in COLUMNAS.items():
        ruta = os.path.join(MODELOS_DIR, relativo.replace("/", os.sep))
        if not os.path.exists(ruta):
            continue
        texto = "\n".join(leer(ruta))
        for par in pares:
            esperado = par[indice]
            viejo = par[1 - indice]
            if 'name = "%s"' % viejo in texto:
                pendientes.append((relativo, viejo, esperado))

    return pendientes


def cambiar_columnas(motor, solo_revisar):
    # cambia el nombre de esas columnas dentro del @Column
    cambios = revisar_columnas(motor)
    if solo_revisar or not cambios:
        return cambios

    for relativo, viejo, esperado in cambios:
        ruta = os.path.join(MODELOS_DIR, relativo.replace("/", os.sep))
        lineas = leer(ruta)
        for i, linea in enumerate(lineas):
            if 'name = "%s"' % viejo in linea:
                lineas[i] = linea.replace('name = "%s"' % viejo, 'name = "%s"' % esperado)
        guardar(ruta, lineas)

    return cambios


IMPORT_UUID = "import java.util.UUID"


def arreglar_import(lineas, esperado):
    # el import de UUID solo va cuando el tipo es UUID
    # si sobra, ktfmt lo borra solo y despues no compila al volver
    tiene = IMPORT_UUID in lineas
    comodin = "import java.util.*" in lineas

    if esperado == "UUID" and not tiene and not comodin:
        # lo metemos en su sitio para que quede en orden alfabetico
        for i, linea in enumerate(lineas):
            if linea.startswith("import ") and linea > IMPORT_UUID:
                lineas.insert(i, IMPORT_UUID)
                return lineas
        # si no habia ninguno mas grande va al final del bloque
        ultimo = max(i for i, l in enumerate(lineas) if l.startswith("import "))
        lineas.insert(ultimo + 1, IMPORT_UUID)

    elif esperado != "UUID" and tiene:
        lineas.remove(IMPORT_UUID)

    return lineas


def cambiar_modelo(ruta, esperado):
    # cambia el @Id, borra la linea comentada y acomoda el import de UUID
    # devuelve (salio_bien, tipo_viejo, mensaje_de_error)
    lineas = leer(ruta)
    salida = []
    viejo = None

    for linea in lineas:
        if LINEA_ID_COMENTADA.match(linea):
            continue  # esta la borramos, el tipo lo maneja env.py
        m = LINEA_ID.match(linea)
        if m and viejo is None:
            # respetamos la sangria, el @Id y la coma como venian
            viejo = m.group(3)
            salida.append("%s%sval id: %s? = null%s" % (
                m.group(1), m.group(2) or "", esperado, m.group(4)))
        else:
            salida.append(linea)

    if viejo is None:
        return False, None, "no encontre la linea del @Id"

    salida = arreglar_import(salida, esperado)
    guardar(ruta, salida)
    return True, viejo, None


# ==========================================================================
#  COMANDO ps
#  dice en que motor estamos y si todo esta parejo
# ==========================================================================


def cmd_ps():
    motor = motor_activo()
    if motor is None:
        print("  no pude saber en que motor estas")
        print("  revisa la linea spring.datasource.url en application.properties")
        return 1

    cfg = MOTORES[motor]
    lineas = leer(PROPERTIES)
    print()
    print("  MOTOR ACTIVO: %s" % motor)
    print()
    print("  application.properties")
    print("    url        %s" % valor_activo(lineas, "spring.datasource.url"))
    print("    usuario    %s" % valor_activo(lineas, "spring.datasource.username"))
    print("    driver     %s" % valor_activo(lineas, "spring.datasource.driver-class-name"))
    print("    dialecto   %s" % valor_activo(lineas, "spring.jpa.database-platform"))
    print()
    print("  build.gradle.kts")
    for linea in leer(GRADLE):
        limpia = linea.strip()
        if limpia.startswith("runtimeOnly(") and ("jdbc" in limpia or "postgresql" in limpia):
            print("    driver     %s" % limpia)
            break

    print()
    print("  SERVICIOS")
    for nombre, puerto, ruta in SERVICIOS:
        print("    %-16s %s ...... %s" % (nombre, puerto, esta_corriendo(puerto, ruta)))

    bien, mal, rotos, sucios = revisar_modelos(motor)
    columnas = revisar_columnas(motor)
    print()
    print("  MODELOS (%s)" % len(MODELOS))
    print("    en su tipo correcto ... %s" % len(bien))
    print("    fuera de sitio ........ %s" % len(mal))
    print("    sin reconocer ......... %s" % len(rotos))
    print("    con linea comentada ... %s" % len(sucios))
    print("    columnas por cambiar .. %s" % len(columnas))

    if not mal and not rotos and not columnas and not sucios:
        print("    OK, todo consistente con %s" % motor)
    if sucios:
        print()
        print("  ESTOS TIENEN LA LINEA VIEJA COMENTADA ARRIBA:")
        for relativo in sucios:
            print("    %s" % relativo)
        print()
        print("  limpialos con:  python env.py %s" % motor)
    if columnas:
        print()
        print("  COLUMNAS con otro nombre:")
        for relativo, viejo, esperado in columnas:
            print("    %-44s %s -> %s" % (relativo, viejo, esperado))
    if mal:
        print()
        print("  ATENCION: estos no coinciden con %s" % motor)
        for relativo, tiene, esperado in mal:
            print("    %-44s %-5s deberia ser %s" % (relativo, tiene, esperado))
        print()
        print("  RoQui no va a arrancar asi. corrige con:  python env.py %s" % motor)
    if rotos:
        print()
        print("  SIN RECONOCER:")
        for relativo, motivo in rotos:
            print("    %-44s %s" % (relativo, motivo))
    print()
    return 0



# ==========================================================================
#  COMANDOS mariadb y postgres
#  los unicos que modifican archivos
# ==========================================================================


def cmd_cambiar(destino):
    actual = motor_activo()

    # si ya estamos en ese motor solo salimos cuando de verdad no falta nada
    # asi si quedo un archivo a medias, este comando lo termina
    if actual == destino:
        bien, mal, rotos, sucios = revisar_modelos(destino)
        pendiente = mal or rotos or sucios or revisar_columnas(destino)
        if not pendiente:
            print()
            print("  ya estas en %s, no hay nada que hacer" % destino)
            print("  revisa con:  python env.py ps")
            print()
            return 0
        print()
        print("  ya estas en %s pero quedaron cosas a medias, las termino" % destino)

    print()
    # si la base no responde cortamos aqui, sin tocar ni un archivo
    if not probar_conexion(destino):
        print()
        print("  CANCELADO. no se cambio ningun archivo.")
        print()
        return 1

    print()
    cambios = cambiar_properties(destino, False)
    print("  application.properties ........... %s lineas" % len(cambios))
    cambios = cambiar_gradle(destino, False)
    print("  build.gradle.kts ................. %s lineas" % len(cambios))
    columnas = cambiar_columnas(destino, False)
    for relativo, viejo, esperado in columnas:
        print("  columna en %s" % relativo)
        print("    %s -> %s" % (viejo, esperado))

    indice = 0 if destino == "postgres" else 1
    hechos, errores = 0, []

    print()
    print("  modelos:")
    for relativo, tipos in MODELOS.items():
        ruta = os.path.join(MODELOS_DIR, relativo.replace("/", os.sep))
        esperado = tipos[indice]

        if not os.path.exists(ruta):
            errores.append((relativo, "no existe el archivo", None, esperado))
            print("    ERROR  %s" % relativo)
            print("           no existe el archivo")
            continue

        tiene = tipo_actual(ruta)
        # si el tipo ya esta bien pero quedo la linea comentada, igual lo limpiamos
        if tiene == esperado and not necesita_limpieza(ruta, esperado):
            print("    OK     %-44s ya estaba en %s" % (relativo, esperado))
            hechos += 1
            continue

        ok, viejo, error = cambiar_modelo(ruta, esperado)
        if ok:
            print("    OK     %-44s %s -> %s" % (relativo, viejo, esperado))
            hechos += 1
        else:
            errores.append((relativo, error, tiene, esperado))
            print("    ERROR  %s" % relativo)
            print("           %s" % error)
            print("           esperaba:  @Id val id: %s? = null" % tipos[1 - indice])

    print()
    if errores:
        print("  " + "-" * 50)
        print("  %s cambiados, %s con error" % (hechos, len(errores)))
        print()
        print("  ESTOS QUEDARON EN EL TIPO VIEJO:")
        for relativo, error, tiene, esperado in errores:
            print("    %-44s deja la linea asi:" % relativo)
            print("    %-44s   @Id val id: %s? = null" % ("", esperado))
        print()
        print("  RoQui no va a arrancar hasta que los arregles a mano.")
        print()
        return 1

    print("  %s cambiados" % hechos)
    print()
    print("  AHORA REINICIA RoQui:  gradle bootRun")
    print()
    return 0


# ==========================================================================
#  COMANDO check
#  dice que cambiaria, sin tocar ningun archivo
# ==========================================================================


def cmd_check(destino):
    actual = motor_activo()
    cfg = MOTORES[destino]
    print()
    print("  MODO CHECK - no se modifica ningun archivo")

    if actual == destino:
        print("  ya estas en %s" % destino)
    else:
        print("  de %s a %s" % (actual, destino))

    # primero probamos que la base del destino este viva
    print()
    if not probar_conexion(destino):
        print()
        return 1

    print()
    print("  application.properties")
    cambios = cambiar_properties(destino, True)
    if not cambios:
        print("    nada que cambiar")
    for clave, antes, despues in cambios:
        print("    %s" % clave.split(".")[-1])
        print("       %s" % antes)
        print("    -> %s" % despues)

    print()
    print("  build.gradle.kts")
    cambios = cambiar_gradle(destino, True)
    if not cambios:
        print("    nada que cambiar")
    for antes, despues in cambios:
        print("       %s" % antes)
        print("    -> %s" % despues)

    bien, mal, rotos, sucios = revisar_modelos(destino)
    print()
    print("  MODELOS")
    print("    %s ya estan en su tipo" % len(bien))
    if sucios:
        print("    %s tienen la linea vieja comentada, se les quita" % len(sucios))
    if mal:
        print("    %s hay que cambiar:" % len(mal))
        for relativo, tiene, esperado in mal:
            print("       %-44s %s -> %s" % (relativo, tiene, esperado))
    if rotos:
        print("    %s sin reconocer:" % len(rotos))
        for relativo, motivo in rotos:
            print("       %-44s %s" % (relativo, motivo))

    columnas = revisar_columnas(destino)
    print()
    print("  COLUMNAS con otro nombre")
    if not columnas:
        print("    nada que cambiar")
    for relativo, viejo, esperado in columnas:
        print("       %-44s %s -> %s" % (relativo, viejo, esperado))
    print()
    return 0












# ==========================================================================
#  ARRANQUE
#  lee el comando que escribiste y llama al que toca
# ==========================================================================


def ayuda():
    print(__doc__)
    return 1


def main():
    if len(sys.argv) < 2:
        return ayuda()

    comando = sys.argv[1].lower()

    if comando == "ps":
        return cmd_ps()

    if comando == "check":
        if len(sys.argv) < 3 or sys.argv[2].lower() not in MOTORES:
            print()
            print("  falta decir a cual:")
            print("    python env.py check mariadb")
            print("    python env.py check postgres")
            print()
            return 1
        return cmd_check(sys.argv[2].lower())

    if comando in MOTORES:
        return cmd_cambiar(comando)

    return ayuda()


if __name__ == "__main__":
    sys.exit(main())
