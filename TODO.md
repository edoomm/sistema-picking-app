Por hacer:

1. **Contenedores** (Dani)
   1. Verificar que el contenedor escaneado este en la BD
   2. No permitir que se pueda repetir el contenedor (cada sucursal debe tener su propio contenedor)
   3. Visualizar contenedores que se asignaron (nuevo apartado en el menu de los 3 puntitos del PickUp)
   4. Dejar que el operador pueda empezar el PickUp si ya tiene al menos un Contenedor asignado.
   5. Posibilitar el cambio de contenedor (si es que no se ha empezado el PickUp, es decir, si no hay transacciones con ese contenedor)
   6. Obtener ID contenedor

2. **Pickup** (Doni)
   1. Mostrar solamente CardViews que tengan un contenedor ya asignado
   2. Mostrar productos a recolectar por prioridad (del 1 a x, es con un simple *query* con *order by*)
   3. Mostrar contenedor correspondiente asignado (en Operador_has_control) al escanear
   4. Volver a poder escanear un producto que se reportó como faltante
   5. No mastrar aquellos CardViews que ya han sido escaneados
   6. Confirmar Unidad de Medida (cuando UM > 1), poder hacer el cambio si es que el paquete no cuenta con la UM correspondiente
   7. Mostrar 1 CardView para *n* cantidad de SKUs, en lugar de *n* CardViews para *n* SKUs a escanear
      1. Siendo n : apartado
   8. Sonidos de error, cuando no se escanee el SKU o contenedor correspondiente

3. **Almacen** (Magallón)
   1. Reabastecer
   2. Cambiar ubicación (nuevo botón para lider de almacén)
      1. Escanear producto y nueva ubicación
   3. Cambiar información de "VER LISTA COMPLETA" por información concreta
   4. Actualización de controles asignados
      1. Si se le asigna un nuevo control al operador, a través de este botón se podrá asignar nuevos contenedores para las sucursales y a su vez aparecerán
   5. Verificar contenedores? (No recuerdo para que era esa parte)
   
4. **FAQ** (Luis)
   1. Creación del contenido para el FAQ (a lo mejor y necesitemos crear una nueva tabla en la BD)
   2. Correcto acomodo del layout del XML (puedes usar de referencia el de [youtube](https://support.google.com/youtube/?hl=en#topic=9257498))
   3. Actualizaciones del layout de preguntas (periodicamente y lógica para actualizar en la app)
   4. Posibilidad de agregar screenshots al reporte

5. **Tutorial y login (app) + Registro lider de almacen, backdoor, # Apartados ... (web)** (Eduardo)
   1. Correccción de Layouts
   2. Diseño del menu principal
   3. Catalogo de linea de comandos en URLs