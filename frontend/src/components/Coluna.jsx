import { useDroppable } from '@dnd-kit/core'
import { Plus } from 'lucide-react'

export default function Coluna({ coluna, quantidade, arrastado, criando, onAlternarCriacao, formulario, children }) {
  const { setNodeRef, isOver } = useDroppable({ id: coluna.id })
  const vemDeOutraColuna = arrastado && arrastado.coluna !== coluna.id
  const alvo = isOver && vemDeOutraColuna
  const podeCriar = coluna.id === 'A_FAZER'

  return (
    <section
      ref={setNodeRef}
      className={`coluna vidro${alvo ? ' coluna-alvo' : ''}`}
      aria-label={coluna.nome}
      style={{ '--cor-coluna': coluna.cor }}
    >
      <header className="coluna-topo">
        <span className="ponto" />
        <h2>{coluna.nome}</h2>
        <span className="contador">{quantidade}</span>
        {podeCriar && (
          <button
            type="button"
            className="botao-adicionar"
            aria-label={criando ? 'Fechar novo cartão' : 'Novo cartão'}
            aria-expanded={criando}
            onClick={onAlternarCriacao}
          >
            <Plus size={14} />
          </button>
        )}
      </header>

      <div className="coluna-lista">
        {formulario}
        {children}
        {alvo && <div className="solte-aqui">Solte para mover para {coluna.nome}</div>}
        {quantidade === 0 && !formulario && !alvo && (
          <p className="coluna-vazia">
            {vemDeOutraColuna
              ? 'Solte um cartão aqui'
              : podeCriar
                ? 'Nenhum cartão. Use o + para criar.'
                : 'Nenhum cartão aqui'}
          </p>
        )}
      </div>
    </section>
  )
}
