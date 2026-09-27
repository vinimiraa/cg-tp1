# Como usar

## A tela

- **Barra de ferramentas** (topo): botões e opções agrupados em 5 caixas — `algoritmos`, `ferramentas`, `transformações`, `imagem`, `preenchimento`.
- **Área de desenho** (centro): onde os objetos são criados e exibidos. Tem um papel quadriculado de fundo e a origem `(0,0)` marcada com um ponto servem só de referência visual, não interferem no desenho.
- **Barra de status** (rodapé): mostra a coordenada do mouse e o zoom atual.

## Ferramentas (caixa "ferramentas")

Apenas uma fica ativa por vez (botões tipo toggle). Clique num botão para trocar de ferramenta.

- **Mover/Zoom** (padrão ao abrir): arraste com o botão esquerdo (ou o botão do meio, em qualquer ferramenta) para navegar pela área de desenho; use a roda do mouse para dar zoom no ponto sob o cursor.
- **Reta**: clique no ponto inicial, depois no ponto final.
- **Círculo**: clique no centro, depois em qualquer ponto da borda (define o raio).
- **Polígono**: clique para cada vértice; clique com o **botão direito** para fechar o polígono (precisa de pelo menos 3 vértices).
- **Selecionar**: clique em dois pontos opostos para definir um retângulo — todo objeto totalmente contido nele é selecionado (fica destacado) e passa a ser o alvo das transformações.
- **Recorte**: clique em dois pontos opostos para definir a janela de recorte (retângulo azul). A partir daí, toda reta desenhada é recortada contra essa janela usando o algoritmo escolhido em "algoritmos > Recorte".
- **Preencher**: clique dentro de uma região fechada por retas/polígono/círculo para aplicar Flood Fill ou Boundary Fill (algoritmo, cor e conectividade são escolhidos na caixa "preenchimento").

Em qualquer ferramenta de múltiplos cliques (reta, círculo, retângulo de seleção/recorte), o **botão direito** cancela a operação em andamento.

O botão **Limpar tudo** apaga todos os objetos, preenchimentos, seleção e janela de recorte.

## Algoritmos (caixa "algoritmos")

- **Rasterização**: DDA ou Bresenham $\to$ define como as retas são desenhadas pixel a pixel.
- **Recorte**: Cohen-Sutherland ou Liang-Barsky $\to$ define como as retas são cortadas pela janela de recorte.
- **Circunferência**: Bresenham (única opção disponível).
- **Preenchimento**: Flood Fill ou Boundary Fill $\to$ define o algoritmo usado pela ferramenta "Preencher".

Trocar o algoritmo não redesenha o que já existe na tela, só afeta o que for desenhado/preenchido a partir daquele momento.

## Transformações (caixa "transformações")

Atuam sobre os objetos **selecionados** (use a ferramenta "Selecionar" antes). Se nada estiver selecionado, o botão "Aplicar transformação" não faz nada.

- **Translação X / Y**: deslocamento em pixels (sliders de -200 a 200).
- **Rotação**: ângulo em graus (-180 a 180).
- **Escala**: fator multiplicador (0.1x a 5.0x).
- **Aplicar transformação**: aplica rotação e escala em torno do centro da seleção, depois a translação.

## Imagem (caixa "imagem")

Espelha os objetos selecionados em torno do centro da seleção (estilo Paint):

- **Inverter Horizontal**: espelha no eixo Y (esquerda/direita trocam).
- **Inverter Vertical**: espelha no eixo X (cima/baixo trocam).
- **Inverter H+V**: as duas reflexões combinadas (equivalente a uma rotação de 180°).

## Preenchimento (caixa "preenchimento")

Configura a próxima operação da ferramenta "Preencher":

- **Cor**: preto, vermelho (padrão), verde ou azul.
- **Conectividade**: 4-conectado (só vizinhos ortogonais) ou 8-conectado (inclui diagonais) $\to$ afeta como o preenchimento se propaga por cantos "apertados" da região.

Os preenchimentos ficam registrados na cena e são refeitos automaticamente a cada redesenho (por exemplo, ao redimensionar a janela), então não desaparecem.
