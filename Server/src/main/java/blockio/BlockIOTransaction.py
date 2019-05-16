import sys
from block_io import BlockIo
b = BlockIo('6bef-475f-4d48-2370', 'TrupeDoBitBox', 2)
print(b.withdraw_from_addresses(amounts=sys.argv[1], from_addresses=sys.argv[2], to_addresses=sys.argv[3]))