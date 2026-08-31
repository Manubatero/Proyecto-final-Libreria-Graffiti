[TFI_ALVAREZ_BARRERA
**Universidad Tecnológica Nacional**

Tecnicatura Universitaria en Programación

			  
						

**Informe**

**"*Trabajo Final*"**

**Alumnos**: Milton Alvarez \- Manuel Alejandro Barrera  
**Tutor:** Santiago Fonzo   
**Año**: 2026

# **TUP**

**Proyecto:** Sistema de Gestión de Stock, Ventas y Facturación

**Cliente / Comercio:** Librería y Regalería "Graffiti"

## **1\. Definición del Problema e Impacto en el Negocio**

En comercios minoristas con un volumen amplio y heterogéneo de artículos, como es el caso de la librería y regalería **"Graffiti"**, la gestión manual o informal de los procesos clave genera problemas operativos cuyo impacto directo afecta la rentabilidad y la sostenibilidad del negocio:

* **Falta de control de inventario en tiempo real:**  
  * *Problema:* Pérdida de visibilidad sobre las existencias reales por producto.  
  * *Impacto en el negocio:* Inmovilización de capital por sobrecompra de artículos de baja rotación y desfasajes entre el inventario físico y los registros.  
* **Quiebres de stock por falta de alertas tempranas:**  
  * *Problema:* Agotamiento de productos de alta demanda (como bolígrafos, cuadernos o insumos escolares/comerciales) sin previo aviso de reposición.  
  * *Impacto en el negocio:* **Pérdida directa de ventas y de facturación.** La imposibilidad de satisfacer la demanda inmediata deriva en que el cliente recurra a la competencia.  
* **Lentitud e inconsistencia en la atención al cliente:**  
  * *Problema:* Búsqueda manual de precios, cálculo manual de totales y emisión de comprobantes no automatizados.  
  * *Impacto en el negocio:* **Deterioro de la experiencia de compra y pérdida de fidelidad del cliente.** Las demoras en el punto de venta generan filas, insatisfacción y una percepción de informalidad o falta de eficiencia en el comercio.

  ## **2\. Propuesta de Solución y Valor Agregado**

La propuesta consiste en desarrollar un sistema de escritorio centralizado para la administración de inventario, ventas y facturación interna. Más allá de sus características de software, esta solución genera valor tangible para el negocio en los siguientes ejes:

* **Optimizaciones del proceso de venta (Atención ágil):** La captura de artículos mediante lectura de código de barras elimina la búsqueda manual y reduce los tiempos de cobro. Esto se traduce en una atención fluida, reducción de filas y mejor retención de clientes.  
* **Garantía de disponibilidad de productos:** El módulo de alertas automatizadas por umbrales mínimos actúa como un mecanismo preventivo de compras, asegurando la continuidad del stock clave y protegiendo el flujo de ingresos del comercio.  
* **Certeza operativa y reducción de errores:** La automatización en la suma de montos y desglose de tickets elimina el margen de error humano en el cobro, protegiendo la caja del negocio y ofreciendo transparencia al comprador.

  ## 

  ## **3\. Justificación del Stack Tecnológico y Criterios de Selección**

La elección de tecnologías no solo responde a criterios funcionales, sino a una evaluación de costos, riesgos y competencias del equipo:

* **Lenguaje de Programación (Java):**  
  * *Justificación:* Es la tecnología con la que el equipo de desarrollo cuenta con mayor solidez técnica y dominio, lo que minimiza el riesgo de tiempos muertos por curva de aprendizaje. Asimismo, su paradigma orientado a objetos e independencia de plataforma garantizan la estabilidad requerida para un entorno de escritorio comercial.  
* **Base de Datos (MySQL) y Herramienta de Gestión (MySQL Workbench):**  
  * *Justificación:* Se requiere un motor relacional consolidado, gratuito y de alto rendimiento para garantizar integridad en transacciones simultáneas de ventas. Workbench se utiliza como entorno gráfico para acelerar el modelado (DER) y la administración en etapa de desarrollo.  
* **Arquitectura de Escritorio Centralizada ("Enlatado") vs. Aplicación Web:**  
  * *Por qué se eligió escritorio centralizado:* Permite un despliegue directo en el equipamiento físico actual del comercio sin incurrir en costos recurrentes de infraestructura en la nube o alojamiento web. Una arquitectura cliente-servidor local garantiza disponibilidad inmediata e independiza la operación del punto de venta de la conectividad a Internet externa.  
  * *Inconvenientes y riesgos asumidos:* Requiere la instalación manual del cliente ejecutable (`.exe`/`.jar`) en cada puesto de trabajo y la administración local del servidor de base de datos MySQL (mantenimiento y copias de seguridad a cargo del comercio).  
  * *Balance de beneficios sobre riesgos:* Para la escala operativa actual de "Graffiti", los costos y la complejidad de mantener una infraestructura web y servidores remotos superan los beneficios. La solución de escritorio local es económicamente viable, autónoma y responde con latencia cero en el punto de cobro.

    ## **4\. Alcance del Proyecto: Inclusiones, Exclusiones y Trabajo Futuro**

Para delimitar con precisión la responsabilidad del proyecto final, se establece la siguiente clasificación:

### **4.1. Funcionalidades Incluidas en la Entrega**

* CRUD completo de productos con asignación de imágenes por ruta (*Path*).  
* Definición de umbrales mínimos de stock y sistema de alertas visuales de reposición.  
* Registro de ventas en punto de cobro mediante captura USB de código de barras (estándar HID).  
* Sumatoria automática, cálculo de comprobantes e impresión de ticket interno no fiscal.  
* Base de datos MySQL centralizada para sincronización entre terminales.

  ### **4.2. Exclusiones Explícitas del Proyecto (Fuera del Alcance)**

* **Venta en línea / E-commerce:** El sistema está acotado exclusivamente a la operación física en el local comercial.  
* **Gestión avanzada de RRHH o Liquidación de Sueldos:** No se incluyen módulos de personal ni control de asistencia.  
* **Sincronización multi-sucursal remota:** El alcance abarca únicamente el funcionamiento dentro de una red de área local (LAN) en un único establecimiento.

  ### **4.3. Trabajo Futuro (Evolución Post-Entrega)**

* **Facturación Electrónica Homologada (ARCA / AFIP):** Conexión vía Web Services para emisión de comprobantes fiscales y liquidación impositiva.  
* **Módulo de Análisis de Precios mediante IA:** Integración de herramientas para análisis de ofertas de proveedores y sugerencias automáticas de compras.

## 

## **5\. Objetivos del Proyecto y Planificación**

### **5.1. Objetivo General**

Desarrollar e implementar un sistema de software de escritorio para la librería y regalería "Graffiti" que automatice el control de inventarios y agilice el proceso de ventas durante el ciclo académico asignado.

### **5.2. Objetivos Específicos**

* **De Gestión e Investigación:** Relevar los procesos actuales de venta e inventario de la librería "Graffiti" para formalizar la especificación de requerimientos en el primer mes de trabajo.  
* **De Diseño y Arquitectura:** Diseñar la arquitectura del sistema y el diagrama Entidad-Relación (DER) en MySQL optimizado para consultas concurrentes.  
* **De Desarrollo:** Construir los módulos de productos, alertas de stock, lectura de código de barras y facturación en Java.  
* **De Calidad y Despliegue:** Realizar pruebas de integración en entorno local y desplegar la versión ejecutable en el equipamiento del comercio, capacitando al usuario final.

### 

### **5.3. Estimación de Duración de Tareas (Cronograma de Trabajo)**

| Fase / Tarea | Descripción | Duración Estimada |
| :---- | :---- | :---- |
| **Fase 1: Levantamiento y Análisis** | Relevamiento de datos con el comercio, definición de requerimientos y alcance. | 1 semanas |
| **Fase 2: Diseño de Arquitectura y BD** | Modelado del DER en MySQL Workbench, diseño de interfaces y estructura en Java. | 2 semanas |
| **Fase 3: Desarrollo \- Módulo Inventario** | Programación del CRUD de productos, manejo de imágenes (path) y lógica de alertas. | 3 semanas |
| **Fase 4: Desarrollo \- Módulo Ventas** | Implementación de interfaz de cobro, integración con lector HID y cálculo de tickets. | 2 semanas |
| **Fase 5: Pruebas y Ajustes** | Pruebas unitarias, simulación de concurrencia y validación de usabilidad. | 1 semanas |
| **Fase 6: Despliegue y Documentación** | Generación del instalador, script de BD, manual de usuario y memoria final. | 1 semanas |
| **Tiempo Total Estimado** |  | **10 semanas** |

