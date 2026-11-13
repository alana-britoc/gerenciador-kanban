const COLUNAS = ['A_FAZER', 'FAZENDO', 'FEITO']

export default function Cartao({ cartao, onMover, onRemover }) {
  const i = COLUNAS.indexOf(cartao.coluna)
  return (
    <div className={`cartao cartao-${cartao.tipo.toLowerCase()}`}>
      <span className="tipo">{cartao.tipo}</span>
      <h3>{cartao.titulo}</h3>
      {cartao.descricao && <p>{cartao.descricao}</p>}
      {cartao.detalhe && <p><strong>{({ TAREFA: 'Critérios de aceite', BUG: 'Passos para reprodução', MELHORIA: 'Justificativa' })[cartao.tipo]}:</strong> {cartao.detalhe}</p>}
      <div className="acoes">
        <button disabled={i === 0} onClick={() => onMover(cartao.id, COLUNAS[i - 1])}>←</button>
        <button onClick={() => onRemover(cartao.id)}>remover</button>
        <button disabled={i === COLUNAS.length - 1} onClick={() => onMover(cartao.id, COLUNAS[i + 1])}>→</button>
      </div>
    </div>
  )
}
