# Clases java principales
Este directorio contiene las clases `java` que hacen funcionar la aplicación móvil

## [PickUpFragment.java](PickUpFragment.java)
*Fragment* que sirve para realizar la operación de *Pick Up*

### Creación de *cardviews* y cambio en el planograma
A través del método `setViewPagerUp` se inicializa la metodología para poder hacer posible el cambio de ubicaciones, a través del cambio de imagen del `ImageView` con el método `setImageResource`.

Para poder definir lo que irá dentro del `CardView` es necesario tener un arreglo de un `Model`, modelo que definirá los atributos que irán dentro del `CardView`. Solo lo que se muestra es el **título** y **descripción**, sin embargo, puede contener más elementos ([hasta imágenes puede contener](https://www.youtube.com/watch?v=UsXv6VRqZKs&ab_channel=HaerulMuttaqin)). **DEBERÁ DE IGUAL MANERA CONTENER EL** `id_linea` **y** `genérico` **VISIBLES ACORDADOS EN LA JUNTA DEL 27-12-20**.

Dentro del `Model` por conveniencia se definió un atributo `location` que tendrá la `PK` de la tabla `Ubicacion` de la [base de datos](https://github.com/edoomm/sistema-picking-web/blob/master/diagramasBD/database.sql).

![tblubicacion!](https://lh3.googleusercontent.com/pw/ACtC-3f4EtP0Pj8ed1zm2EVbm8xejKcTNgZRGdWQ8ezB61xWx2lP30GsjKntjrk3I7IdhvfxGNYOWjBmUUa_kKZI9gecCsZFsC2sfN5Dz08-ukAhc_WK5MYpiDz3Mp9bKje4oYllCYj0TvYFzehqWDMlO70s=w433-h415-no?authuser=0)

A través de este `PK` es posible (con ayuda del equipo de web, que definirá los atributos: `pasillo`, `rack`, etc.) poder acceder a la información seccionada de la ubicación, para así poder cambiar la información que de igual forma se despliega en las pantallas de la aplicación.

![ejemlocacion!](https://lh3.googleusercontent.com/pw/ACtC-3dFqVy0Yt6ub8C_--lApj17_pny1TCrgmCyqiDYq9FB9sbm8bjmDoFUv30V-GeTMIVxrddZJgD7afP1iY45JshxSO7kISw1y1j62zJ5rDHJ3NJjCWKe_AfE-b8m5m2O8d0SxxNCy2XuAfWIpg_M2pxk=w576-h150-no?authuser=0)

**Para poder conseguir el efecto de cambio de ubicación** se cambian las imagenes, todas [ya se encuentran en el repositorio](../../../../res/drawable/), llevan por nombre `planogra[ma|m]_[# col]_[# fila].png` y con ayuda del método `setOnPageChangeListener` (depreciado, se deberá cambiar en un futuro) y la creación del objeto `ViewPager.OnPageChangeListener` con sobreescritura en su método `onPageSelected(int position)` es posible a través de la lista de modelos obtener el `CardView` seleccionado y con él, su ubicación.

```java
// Ejemplo para obtener la ubicación de cualquier CardView
@Override
public void onPageSelected(int position) {
    String ubi = models.get(position).getLocation();

    /*  Pseudocódigo para cambiar datos en pantalla  */
    var result = Database.Select("SELECT * FROM Ubicacion WHERE ubicacion = " + ubi);
    txtPasillo.setText(result["pasillo"]);
    txtRack.setText(result["rack"]);
    planograma.setImageResource(SeleccionadorPlanograma(ubi).getDrawable());
}
```

Con esto, quedan cosas pendientes:
- [ ] Creación de clase para la Base de Datos (conexión y queries)
- [ ] Creacioón de clase seleccionadorá de imágnes del planograma