BSTNode * BST::Insert(BSTNode * node, int key)
{
    // If BST doesn't exist
    // create a new node as root
    // or it's reached when
    // there's no any child node
    // so we can insert a new node here
    if(node == NULL)
    {
        node = new BSTNode;
        node->Key = key;
        node->Left = NULL;
        node->Right = NULL;
        node->Parent = NULL;
    }
    // If the given key is greater than
    // node's key then go to right subtree
    else if(node->Key Right = Insert(node->Right, key);
        node->Right->Parent = node;
    }
    // If the given key is smaller than
    // node's key then go to left subtree
    else
    {
        node->Left = Insert(node->Left, key);
        node->Left->Parent = node;
    }
return node;
}