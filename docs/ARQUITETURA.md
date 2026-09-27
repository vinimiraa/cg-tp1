# Organização do projeto

Java 17 + Swing, sem frameworks externos além do Lombok (`@Getter`/`@Setter`/`@AllArgsConstructor`, para reduzir boilerplate).

## Pacotes

```
com.puc.cg
├── algorithms/         algoritmos puros, sem dependência de Swing/UI
│   ├── raster/         rasterização de retas e círculos (DDA, Bresenham)
│   ├── clipping/       recorte de retas contra uma janela (Cohen-Sutherland, Liang-Barsky)
│   ├── filling/        preenchimento de regiões (Flood Fill, Boundary Fill)
│   └── transform/      matrizes 3x3 (Matrix3) e fábricas de transformação (Transformations)
├── commons/
│   ├── models/          tipos de dados do domínio (Point2D, Window, Shape e implementações)
│   └── util/            Framebuffer, Scene, Palette, Icons, Dimensions
└── ui/                  Swing: janela, painéis, ferramentas de interação
    ├── screens/         MainFrame, DrawingPanel, ToolPanel
    └── tools/           uma classe por ferramenta de mouse (LineTool, CircleTool, ...)
```

## Padrão Strategy (algoritmos)

Cada família de algoritmo tem uma interface e um enum que implementa essa interface diretamente ("enum como strategy"):

- `LineRasterizerAlgorithm`: enum `LineAlgorithm { DDA, BRESENHAM }`
- `ClipWindowAlgorithm`: enum `ClipAlgorithm { COHEN_SUTHERLAND, LIANG_BARSKY }`
- `FillAlgorithm`: enum `FillMethod { FLOOD_FILL, BOUNDARY_FILL }`

O enum guarda uma instância `impl.*` concreta e delega a chamada. Isso elimina qualquer `if/else`/`switch` para escolher o algoritmo em tempo de execução: o `DrawingPanel` guarda o enum escolhido (`@Setter LineAlgorithm lineAlgorithm`, etc.) e chama `lineAlgorithm.draw(...)` diretamente, não importando qual implementação está por trás.

`Connectivity` (4 ou 8 vizinhos) segue a mesma ideia, mas sem lógica: é só uma tabela de offsets (`int[][]`) usada pelos algoritmos de preenchimento para decidir quais pixels vizinhos visitar.

## Polimorfismo de formas (`Shape`)

`Shape` é a interface implementada por `LineSegment`, `Circle` (records) e `Polygon2D` (classe mutável, guarda uma lista de vértices). Contrato:

```java
void draw(Framebuffer fb, LineRasterizerAlgorithm line, CircleRasterizerAlgorithm circle, int rgb);
Window bounds();
Shape transform(Matrix3 matrix);
```

Isso evita que `Scene`, `Selection` e `SceneRenderer` precisem de um `case` para cada tipo de forma, pois eles só percorrem `List<Shape>` e chamam o método da interface. Cada forma sabe desenhar, calcular seus próprios limites (bounding box) e se transformar.

## Fluxo de dados (um clique até o pixel na tela)

1. `DrawingPanel` (um `JPanel`) recebe o evento de mouse, converte a coordenada de tela para coordenada de modelo via `Viewport.toModelPoint` (desfaz pan/zoom).
2. Delega para a `DrawingTool` da ferramenta ativa (`Tool` $\to$ mapa `Map<Tool, DrawingTool>`). Cada tool implementa só `onMousePressed` e, quando precisa de preview em tempo real (reta, círculo, polígono, retângulo), `onMouseMoved`.
3. A tool chama métodos da interface `DrawingContext` (implementada pelo próprio `DrawingPanel`) para adicionar formas em `Scene`, atualizar o preview (`PreviewState`) ou pedir redesenho (`requestRedraw`).
4. `requestRedraw` chama `SceneRenderer.render(...)`, que limpa o `Framebuffer` e desenha, em ordem: formas da cena (recortadas pela `ClipWindow` se houver), preenchimentos (`FillAction` replay), contorno da janela de recorte, contorno da seleção, preview ativo.
5. `DrawingPanel.paintComponent` desenha a imagem do `Framebuffer` na tela (aplicando a transformação de pan/zoom do `Graphics2D`) e por cima desenha o grid/origem como overlay vetorial — **sem** tocar nos pixels do framebuffer.

## Sistema de coordenadas

O modelo usa `(0,0)` no **centro** do framebuffer (estilo Paint/matemático, não canto superior esquerdo). `Framebuffer.setPixel`/`getPixel` fazem a conversão `pixel = modelo + (largura/2, altura/2)` internamente; todo o resto do código (algoritmos, transformações, ferramentas) trabalha só em coordenadas de modelo.

## Preenchimento persistente (`FillAction`)

Um clique com a ferramenta "Preencher" não pinta o framebuffer direto — cria um `FillAction(seed, método, corPreenchimento, corReferência, conectividade)` guardado em `Scene.fills`. Isso porque o framebuffer é recriado do zero a cada `render()` (ex.: ao redimensionar a janela), então o preenchimento precisa ser "reaplicado" a cada frame para não se perder; `SceneRenderer` faz esse replay depois de desenhar as formas.

## Seleção e transformações

`Selection` guarda a lista de formas selecionadas e calcula o `bounds()`/`center()` delas. `ToolPanel.applyPivoted` monta a matriz `T(centro) · M · T(-centro)` para que rotação/escala/reflexão aconteçam em torno do centro da seleção (estilo Paint), não da origem do canvas. `Selection.applyTransform` substitui cada forma selecionada pela sua versão transformada, tanto na cena quanto na própria seleção (para permitir aplicar transformações em sequência).

## Onde cada algoritmo do curso está implementado

| Algoritmo | Classe |
|---|---|
| DDA | `algorithms/raster/impl/LineRasterizerDDAImpl.java` |
| Bresenham (reta) | `algorithms/raster/impl/LineRasterizerBresenhamImpl.java` |
| Bresenham (círculo) | `algorithms/raster/impl/CircleRasterizerBresenhamImpl.java` |
| Cohen-Sutherland | `algorithms/clipping/impl/CohenSutherland.java` |
| Liang-Barsky | `algorithms/clipping/impl/LiangBarsky.java` |
| Flood Fill (flood4) | `algorithms/filling/impl/FloodFillImpl.java` |
| Boundary Fill (boundary4) | `algorithms/filling/impl/BoundaryFillImpl.java` |
| Translação/Rotação/Escala/Reflexão | `algorithms/transform/Transformations.java` + `Matrix3` |

Flood Fill e Boundary Fill usam pilha explícita (`Deque<int[]>`), não recursão. O pseudocódigo do slide é recursivo, mas recursão real estouraria a stack em regiões grandes.
